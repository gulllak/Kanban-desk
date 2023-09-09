package ru.evgenii.kanban.services;

import ru.evgenii.kanban.services.impl.FileBackedTasksManager;
import ru.evgenii.kanban.services.impl.InMemoryHistoryManager;
import ru.evgenii.kanban.services.impl.InMemoryTaskManager;

import java.nio.file.Paths;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTasksManager() {
        return new FileBackedTasksManager(Paths.get("src/main/resources/backup.csv").toFile());
    }
}
