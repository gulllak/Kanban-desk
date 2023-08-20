package ru.evgenii.kanban.services.impl;

import ru.evgenii.kanban.exeptions.ManagerSaveException;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.FileSaver;
import ru.evgenii.kanban.services.HistoryManager;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.utils.TaskStatus;
import ru.evgenii.kanban.utils.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    final File fileName;
    private final FileSaver fileSaver = new FileSaver();

    public FileBackedTasksManager(File fileName) {
        this.fileName = Paths.get("src/main/resources/" + fileName).toFile();
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getFileBackedTasksManager();

        Task task1 = new Task("Помыть кота", "Берем кота и моем");
        Task task2 = new Task("Сходить к зубному", "Запись на 20:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //---------------------------------

        Epic epic1 = new Epic("Написать код программы", "Янедкс Практикум");
        Subtask subtask1 = new Subtask("Прочитать ТЗ", "Документ", epic1);
        Subtask subtask2 = new Subtask("Написать код", "Java", epic1);
        Subtask subtask3 = new Subtask("Протестировать код", "Java", epic1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        //---------------------------------

        Epic epic2 = new Epic("Сходить в магазин", "Лента");
        taskManager.addEpic(epic2);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(6);
        taskManager.getEpicById(3);
        taskManager.getEpicById(7);

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(3);

        System.out.println("Работа программы ДО");

        taskManager.getAllTask().forEach(System.out::println);
        taskManager.getAllEpic().forEach(System.out::println);
        taskManager.getAllSubtask().forEach(System.out::println);

        taskManager.getHistoryManager().getHistory().forEach(s -> System.out.print(s.getId() + ", "));


        //---------------------------------
        System.out.println("\n");
        System.out.println("Работа программы ПОСЛЕ восстановления:");

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(Paths.get("backup.csv").toFile());

        fileBackedTasksManager.getAllTask().forEach(System.out::println);
        fileBackedTasksManager.getAllEpic().forEach(System.out::println);
        fileBackedTasksManager.getAllSubtask().forEach(System.out::println);

        System.out.println();

        fileBackedTasksManager.getHistoryManager().getHistory().forEach(s -> System.out.print(s.getId() + ", "));
    }

    private void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(fileSaver.getHeader());
            writer.newLine();

            for (Task  task : tasks.values()) {
                writer.write(fileSaver.toString(task));
                writer.newLine();
            }

            for (Task  task : epics.values()) {
                writer.write(fileSaver.toString(task));
                writer.newLine();
            }

            for (Task  task : subtasks.values()) {
                writer.write(fileSaver.toString(task));
                writer.newLine();
            }

            writer.newLine();

            writer.write(fileSaver.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла");
        }
    }

    static FileBackedTasksManager  loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        Map<Integer, Task> recoveredTasks = new HashMap<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileBackedTasksManager.fileName))) {
            bufferedReader.readLine();

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if(line.isEmpty()) break;

                Task task = fileBackedTasksManager.fromString(line);
                recoveredTasks.put(task.getId(), task);
            }

            for(Task task : recoveredTasks.values()) {
                if(task.getTaskType() == TaskType.TASK) {
                    fileBackedTasksManager.tasks.put(task.getId(), task);
                } else if(task.getTaskType() == TaskType.EPIC) {
                    fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                } else if(task.getTaskType() == TaskType.SUBTASK) {
                    fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);
                }
            }

            String historyLine = bufferedReader.readLine();
            List<Integer>  history = fileBackedTasksManager.historyFromString(historyLine);

            for (Integer id : history) {
                fileBackedTasksManager.historyManager.add(recoveredTasks.get(id));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBackedTasksManager;
    }

    public Task fromString(String value) {
        Task task = null;

        String[] line = value.split(",");
        switch (line[1].toUpperCase()) {
            case "TASK":
                task = new Task(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()));
                break;
            case "EPIC":
                task = new Epic(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()));
                break;
            case "SUBTASK":
                task = new Subtask(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()), epics.get(Integer.parseInt(line[5])));
                break;
        }
        return task;
    }

    public List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public List<Task> getAllTask() {
        return super.getAllTask();
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return super.getAllSubtask();
    }

    @Override
    public List<Epic> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return super.getEpicSubtasks(epic);
    }
}
