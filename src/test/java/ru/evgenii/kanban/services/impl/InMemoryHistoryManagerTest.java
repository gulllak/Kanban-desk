package ru.evgenii.kanban.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.HistoryManager;
import ru.evgenii.kanban.services.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task 1", "Description 1");
        task2 = new Task("Task 2", "Description 2");
        task3 = new Task("Task 3", "Description 3");

        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    void add() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История не равна 3");
    }

    @Test
    void removeFromFirst() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История не равна 3");
        assertEquals(task1, history.get(0), "Задача не Task1");

        historyManager.remove(1);

        history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не равна 2");
        assertFalse(history.contains(task1), "Первая задача не удалилась");
    }

    @Test
    void removeFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История не равна 3");
        assertEquals(task2, history.get(1), "Задача не Task2");

        historyManager.remove(2);

        history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не равна 2");
        assertFalse(history.contains(task2), "Задача из середины не удалилась");
    }

    @Test
    void removeFromLast() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История не равна 3");
        assertEquals(task3, history.get(2), "Задача не Task3");

        historyManager.remove(3);

        history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не равна 2");
        assertFalse(history.contains(task3), "Задача в конце не удалилась");
    }

    @Test
    void getEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(0, history.size());
    }

    @Test
    void getNotEmptyAndDuplicationHistory() {
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая");
        assertEquals(1, history.size());
    }
}