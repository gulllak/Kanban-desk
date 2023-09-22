package ru.evgenii.kanban.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.servers.HttpTaskServer;
import ru.evgenii.kanban.servers.KVServer;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.services.TaskManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {

    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void init() throws IOException {
        new KVServer().start();
        taskManager = Managers.getHttpTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);

        task = new Task("Помыть кота", "Берем кота и моем", "2023-09-01T17:00:00", 10);
        taskManager.addTask(task);

        epic = new Epic("Написать код программы", "Янедкс Практикум");
        taskManager.addEpic(epic);

        subtask = new Subtask("Написать код", "Java", epic,"2023-09-02T15:00:00", 120);
        taskManager.addSubtask(subtask);
    }

    @Test
    void saveAndLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager("http://localhost:8078");

        assertEquals(taskManager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(), "Приоритеты не равны");
        assertEquals(taskManager.getAllTask(), httpTaskManager.getAllTask(), "Задачи не равны");
        assertEquals(taskManager.getAllSubtask(), httpTaskManager.getAllSubtask(), "Подадачи не равны");
        assertEquals(taskManager.getAllEpic(), httpTaskManager.getAllEpic(), "Эпики не равны");
        assertEquals(taskManager.getHistoryManager().getHistory(), httpTaskManager.getHistoryManager().getHistory(), "История не равны");
    }
}