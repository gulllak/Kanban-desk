package ru.evgenii.kanban.services.impl;

import ru.evgenii.kanban.exeptions.ManagerSaveException;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.Formatter;
import ru.evgenii.kanban.services.HistoryManager;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.utils.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    final File fileName;
    private final Formatter formatter = new Formatter();

    public FileBackedTasksManager(File fileName) {
        this.fileName = Paths.get(fileName.toURI()).toFile();
    }

     void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(formatter.getHeader());
            writer.newLine();

            for (Task  task : tasks.values()) {
                writer.write(formatter.toString(task));
                writer.newLine();
            }

            for (Task  task : epics.values()) {
                writer.write(formatter.toString(task));
                writer.newLine();
            }

            for (Task  task : subtasks.values()) {
                writer.write(formatter.toString(task));
                writer.newLine();
            }

            writer.newLine();

            writer.write(formatter.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла");
        }
    }

     static FileBackedTasksManager  loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        Map<Integer, Task> recoveredTasks = new LinkedHashMap<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileBackedTasksManager.fileName))) {
            if(bufferedReader.readLine() == null) {
                return new FileBackedTasksManager(file);
            }

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if(line.isEmpty()) break;

                Task task = fileBackedTasksManager.formatter.fromString(line, recoveredTasks);
                recoveredTasks.put(task.getId(), task);
            }

            for(Task task : recoveredTasks.values()) {
                if(task.getId() > fileBackedTasksManager.idInc) {
                    fileBackedTasksManager.idInc = task.getId();
                }
                if(task.getTaskType() == TaskType.TASK) {
                    fileBackedTasksManager.tasks.put(task.getId(), task);
                    fileBackedTasksManager.prioritizedTasks.add(task);
                } else if(task.getTaskType() == TaskType.EPIC) {
                    fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                } else if(task.getTaskType() == TaskType.SUBTASK) {
                    int epicId = ((Subtask) task).getEpic().getId();
                    fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);
                    fileBackedTasksManager.epics.get(epicId).getSubtasks().add((Subtask) task);
                    fileBackedTasksManager.prioritizedTasks.add(task);
                    fileBackedTasksManager.updateEpicTime(((Subtask) task).getEpic());
                }
            }

            String historyLine = bufferedReader.readLine();
            if(historyLine != null) {
                List<Integer> history = fileBackedTasksManager.formatter.historyFromString(historyLine);

                for (Integer id : history) {
                    fileBackedTasksManager.historyManager.add(recoveredTasks.get(id));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBackedTasksManager;
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
        if(addedTask == null) {
            return null;
        }
        save();
        return addedTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        if(addedSubtask == null) {
            return null;
        }
        save();
        return addedSubtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        if(addedEpic == null) {
            return null;
        }
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

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {
        return super.getAllSubtasksByEpicId(id);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}
