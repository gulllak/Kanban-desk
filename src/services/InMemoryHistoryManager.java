package services;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        checkHistorySize();
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    private void checkHistorySize() {
        if(history.size() == 10) {
            history.remove(0);
        }
    }
}
