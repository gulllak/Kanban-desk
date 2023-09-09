package ru.evgenii.kanban.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.evgenii.kanban.exeptions.ValidationIntersectionException;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.utils.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    public void init() {
        task1 = new Task("Помыть кота", "Берем кота и моем", "2023-09-01T17:00:00", 10);
        task2 = new Task("Сходить к зубному", "Запись на 20:00");
        epic = new Epic("Написать код программы", "Янедкс Практикум");
        subtask1 = new Subtask("Написать код", "Java", epic,"2023-09-02T15:00:00", 120);
        subtask2 = new Subtask("Протестировать код", "Java", epic, "2023-09-02T17:00:00", 120);
    }

    @Test
    void getAllTask() {
        List<Task> tasks = taskManager.getAllTask();
        assertEquals(0, tasks.size());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        tasks = taskManager.getAllTask();
        assertEquals(2, tasks.size());
    }

    @Test
    void getAllSubtask() {
        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(0, subtasks.size());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        subtasks = taskManager.getAllSubtask();
        assertEquals(2, subtasks.size());
    }

    @Test
    void getAllEpic() {
        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(0, epics.size());

        taskManager.addEpic(epic);
        epics = taskManager.getAllEpic();
        assertEquals(1, epics.size());
    }

    @Test
    void deleteAllTask() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> tasks = taskManager.getAllTask();
        assertEquals(2, tasks.size());

        taskManager.deleteAllTask();
        tasks = taskManager.getAllTask();
        assertEquals(0, tasks.size());

    }

    @Test
    void deleteAllSubtask() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(2, subtasks.size());

        taskManager.deleteAllSubtask();
        subtasks = taskManager.getAllSubtask();
        assertEquals(0, subtasks.size());
    }

    @Test
    void deleteAllEpic() {
        taskManager.addEpic(epic);
        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(1, epics.size());

        taskManager.deleteAllEpic();
        epics = taskManager.getAllEpic();
        assertEquals(0, epics.size());
    }

    @Test
    void getTaskById() {
        Task nullTask = taskManager.getTaskById(0);
        assertNull(nullTask, "Задача существует");

        nullTask = taskManager.getTaskById(999999);
        assertNull(nullTask, "Задача существует");

        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskById(1));

    }

    @Test
    void getEpicById() {
        Epic nullEpic = taskManager.getEpicById(0);
        assertNull(nullEpic, "Эпик существует");

        nullEpic = taskManager.getEpicById(999999);
        assertNull(nullEpic, "Эпик существует");

        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(1));
    }

    @Test
    void getSubtaskById() {
        Subtask nullSubtask = taskManager.getSubtaskById(0);
        assertNull(nullSubtask, "Подзадача существует");

        nullSubtask = taskManager.getSubtaskById(999999);
        assertNull(nullSubtask, "Подзадача существует");

        taskManager.addSubtask(subtask1);
        assertEquals(subtask1, taskManager.getSubtaskById(1));
        assertEquals(epic, taskManager.getSubtaskById(1).getEpic());
    }

    @Test
    void addTask() {
        assertNotNull(taskManager.getAllTask(), "Список не пустой");

        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskById(1), "Задача не добавилась");
    }

    @Test
    void addEpic() {
        List<Subtask> subtasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        assertEquals(epics, taskManager.getAllEpic(), "Список не пустой");

        epics.add(taskManager.addEpic(epic));
        assertEquals(1, taskManager.getAllEpic().size(), "Список пустой");

        assertEquals(epic, taskManager.getEpicById(1));
        assertEquals(subtasks, taskManager.getEpicById(1).getSubtasks());
    }

    @Test
    void addSubtask() {
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(subtasks, taskManager.getAllSubtask(), "Список не пустой");

        subtasks.add(taskManager.addSubtask(subtask1));
        assertEquals(1, taskManager.getAllSubtask().size(), "Список пустой");

        assertEquals(subtask1, taskManager.getSubtaskById(1));
        assertEquals(epic, taskManager.getSubtaskById(1).getEpic(), "Эпики не совпадают");
    }

    @Test
    void updateTask() {
        Task task = taskManager.updateTask(task1);
        assertNull(task, "Задача существует");

        taskManager.addTask(task1);
        task = taskManager.updateTask(task1);
        assertNotNull(task, "Задача не существует");

        task1.setName("Помыть собаку");
        task = taskManager.updateTask(task1);
        assertEquals(task, taskManager.getTaskById(1), "Таска не обновилась");
    }

    @Test
    void updateSubtask() {
        Subtask subtask = taskManager.updateSubtask(subtask1);
        assertNull(subtask, "Подзадача существует");

        taskManager.addSubtask(subtask1);
        subtask = taskManager.updateSubtask(subtask1);
        assertNotNull(subtask, "Подзадачи не существует");

        subtask1.setName("Изменил имя");
        subtask1.setDescription("Изменил описание");
        subtask = taskManager.updateSubtask(subtask1);
        assertEquals(subtask, taskManager.getSubtaskById(1), "Подзадача не обновилась");
    }

    @Test
    void updateEpic() {
        Epic epicNull = taskManager.updateEpic(epic);
        assertNull(epicNull, "Эпик существует");

        taskManager.addEpic(epic);
        Epic epicNotNull = taskManager.updateEpic(epic);
        assertNotNull(epicNotNull, "Эпика не существует");

        epic.setName("Изменили");
        epicNotNull = taskManager.updateEpic(epic);
        assertEquals(epicNotNull, taskManager.getEpicById(1), "Эпик не обновился");
    }
    @Test
    void deleteTaskById() {
        taskManager.addTask(task1);
        List<Task> tasks = taskManager.getAllTask();

        NoSuchElementException elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteTaskById(0);
                    }
                });

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteTaskById(99999);
                    }
                });

        assertEquals(1, tasks.size());
        taskManager.deleteTaskById(1);
        tasks = taskManager.getAllTask();
        assertEquals(0, tasks.size());

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteTaskById(1);
                    }
                });
    }

    @Test
    void deleteSubtaskById() {
        taskManager.addSubtask(subtask1);
        List<Subtask> subtasks = taskManager.getAllSubtask();

        NoSuchElementException elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteSubtaskById(0);
                    }
                });

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteSubtaskById(99999);
                    }
                });

        assertEquals(1, subtasks.size());
        taskManager.deleteSubtaskById(1);
        subtasks = taskManager.getAllSubtask();
        assertEquals(0, subtasks.size());

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteSubtaskById(1);
                    }
                });
    }

    @Test
    void deleteEpicById() {
        taskManager.addEpic(epic);
        List<Epic> epics = taskManager.getAllEpic();

        NoSuchElementException elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteEpicById(0);
                    }
                });

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteEpicById(99999);
                    }
                });

        assertEquals(1, epics.size());
        taskManager.deleteEpicById(1);
        epics = taskManager.getAllEpic();
        assertEquals(0, epics.size());

        elementException = assertThrows(NoSuchElementException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.deleteEpicById(1);
                    }
                });
    }

    @Test
    void deleteSubtasksOfEpicById() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Epic> epics = taskManager.getAllEpic();
        List<Subtask> epicSubtasks = epic.getSubtasks();

        assertEquals(1, epics.size());
        assertEquals(2, epicSubtasks.size());

        taskManager.deleteEpicById(1);

        epics = taskManager.getAllEpic();
        epicSubtasks = taskManager.getAllSubtask();

        assertEquals(0, epics.size());
        assertEquals(0, epicSubtasks.size());
    }

    @Test
    void getEpicSubtasks() {
        taskManager.addEpic(epic);
        List<Subtask> epicSubtasks = epic.getSubtasks();
        assertEquals(0, epicSubtasks.size());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        epicSubtasks = epic.getSubtasks();
        assertEquals(2, epicSubtasks.size());

        taskManager.deleteSubtaskById(2);
        epicSubtasks = epic.getSubtasks();
        assertEquals(1, epicSubtasks.size());
    }

    @Test
    void updateEpicStatus() {
        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(1).getStatus(), "Статус не NEW");
        assertEquals(0, taskManager.getEpicById(1).getSubtasks().size(), "Список не пустой");

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, taskManager.getEpicById(1).getStatus(), "Статус не NEW");
        assertEquals(2, taskManager.getEpicById(1).getSubtasks().size(), "Список пустой");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус не IN_PROGRESS");
        assertEquals(2, taskManager.getEpicById(1).getSubtasks().size(), "Список пустой");

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, taskManager.getEpicById(1).getStatus(), "Статус не DONE");
        assertEquals(2, taskManager.getEpicById(1).getSubtasks().size(), "Список пустой");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус не IN_PROGRESS");
        assertEquals(2, taskManager.getEpicById(1).getSubtasks().size(), "Список пустой");

        taskManager.deleteSubtaskById(3);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус не IN_PROGRESS");
        assertEquals(1, taskManager.getEpicById(1).getSubtasks().size(), "Список пустой");
    }

    @Test
    void validate() {
        taskManager.addTask(task1);
        Task notValidateTask = new Task("Помыть кота 2", "Берем кота и моем 2", "2023-09-01T17:00:00", 10);

        ValidationIntersectionException elementException = assertThrows(ValidationIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.addTask(notValidateTask);
                    }
                });

        notValidateTask.setStartTime(LocalDateTime.parse("2023-09-01T17:09:00"));
        taskManager.updateTask(notValidateTask);

        elementException = assertThrows(ValidationIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.addTask(notValidateTask);
                    }
                });

        notValidateTask.setStartTime(LocalDateTime.parse("2023-09-01T17:10:00"));
        Task newTask = taskManager.updateTask(notValidateTask);
        assertNull(newTask, "Задача есть");

        newTask = taskManager.addTask(notValidateTask);
        assertEquals(notValidateTask, newTask, "Задачи нет");

        taskManager.addSubtask(subtask1);
        notValidateTask.setStartTime(LocalDateTime.parse("2023-09-02T16:00:00"));

        elementException = assertThrows(ValidationIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.updateTask(notValidateTask);
                    }
                });

        subtask1.setStartTime(LocalDateTime.parse("2023-09-01T17:08:00"));
        elementException = assertThrows(ValidationIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.updateSubtask(subtask1);
                    }
                });

    }

    @Test
    void getPrioritizedTasks(){
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);

        assertEquals(3, taskManager.getPrioritizedTasks().size(), "В списке не верное число задач");
        assertNull(taskManager.getPrioritizedTasks().get(2).getStartTime(), "Последняя задача не null");
        assertNotNull(taskManager.getPrioritizedTasks().get(0).getStartTime(), "Первая задача null");

        assertEquals(LocalDateTime.of(2023, 9, 1, 17, 0), taskManager.getPrioritizedTasks().get(0).getStartTime(), "Приоритет не совпадает");
        assertEquals(LocalDateTime.of(2023, 9, 2, 15, 0), taskManager.getPrioritizedTasks().get(1).getStartTime(), "Приоритет не совпадает");
    }
}