package services;

import services.impl.InMemoryHistoryManager;
import services.impl.InMemoryTaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
