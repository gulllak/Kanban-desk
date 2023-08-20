package ru.evgenii.kanban;

import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.utils.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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

        //---------------------------------
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

        taskManager.getHistoryManager().getHistory().forEach(System.out::println);
    }
}
