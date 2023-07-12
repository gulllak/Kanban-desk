package services;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {

    //Чтобы не было возможности создать экземпляр, т.к. класс утилитарный со статическими методами.
    private Managers(){}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }
}
