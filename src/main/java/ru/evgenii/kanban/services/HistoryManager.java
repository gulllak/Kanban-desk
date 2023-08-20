package ru.evgenii.kanban.services;

import ru.evgenii.kanban.models.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
