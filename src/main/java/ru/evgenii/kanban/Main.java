package ru.evgenii.kanban;

import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.servers.HttpTaskServer;
import ru.evgenii.kanban.servers.KVServer;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.services.impl.HttpTaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new KVServer().start();

            TaskManager manager = Managers.getHttpTaskManager();
            HttpTaskServer httpTaskServer = new HttpTaskServer(manager);


            Task task1 = new Task("Помыть кота", "Берем кота и моем", "2023-09-01T12:00:00", 10);
            Task task2 = new Task("Сходить к зубному", "Запись на 20:00");
            manager.addTask(task1);
            manager.addTask(task2);

            Epic epic1 = new Epic("Написать код программы", "Янедкс Практикум");
            Subtask subtask1 = new Subtask("Прочитать ТЗ", "Документ", epic1,"2023-09-01T12:10:00", 10);
            Subtask subtask2 = new Subtask("Написать код", "Java", epic1,"2023-09-02T15:00:00", 120);
            Subtask subtask3 = new Subtask("Протестировать код", "Java", epic1);
            manager.addEpic(epic1);
            manager.addSubtask(subtask1);
            manager.addSubtask(subtask2);
            manager.addSubtask(subtask3);
            manager.getTaskById(1);
            manager.getSubtaskById(4);
            manager.getHistoryManager().getHistory().forEach(System.out::println);

            Epic epic2 = new Epic("Сходить в магазин", "Лента");
            manager.addEpic(epic2);

            HttpTaskManager taskManager = new HttpTaskManager("http://localhost:8078");
            taskManager.getHistoryManager().getHistory().forEach(System.out::println);

            taskManager.getAllTask().forEach(System.out::println);
            taskManager.getAllEpic().forEach(System.out::println);
            taskManager.getAllSubtask().forEach(System.out::println);
            taskManager.getPrioritizedTasks().forEach(System.out::println);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
