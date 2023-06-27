import models.Epic;
import models.Subtask;
import models.Task;
import services.TaskManager;
import utils.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Помыть кота", "Берем кота и моем");
        Task task2 = new Task("Сходить к зубному", "Запись на 20:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //---------------------------------

        Epic epic1 = new Epic("Написать код программы", "Янедкс Практикум");
        Subtask subtask1 = new Subtask("Прочитать ТЗ", "Документ", epic1);
        Subtask subtask2 = new Subtask("Написать код", "Java", epic1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        //---------------------------------

        Epic epic2 = new Epic("Сходить в магазин", "Лента");
        Subtask subtask3 = new Subtask("Купить стейк", "Продукты", epic2);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);

        //---------------------------------
        System.out.println("ДО");
        taskManager.getAllTask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllSubtask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllEpic().forEach(System.out::println);
        System.out.println();

        //---------------------------------
        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        //---------------------------------
        System.out.println("ПОСЛЕ");

        taskManager.getAllTask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllSubtask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllEpic().forEach(System.out::println);

        //---------------------------------

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);

        //---------------------------------
        System.out.println("УДАЛИЛИ ЗАДАЧУ И ЭПИК");

        taskManager.getAllTask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllSubtask().forEach(System.out::println);
        System.out.println();
        taskManager.getAllEpic().forEach(System.out::println);
    }
}
