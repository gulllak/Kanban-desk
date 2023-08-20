package ru.evgenii.kanban.services;

import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import java.util.List;

public interface TaskManager {

    HistoryManager getHistoryManager();
    List<Task> getAllTask();

    List<Subtask> getAllSubtask();

    List<Epic> getAllEpic();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    Task addTask(Task task);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    List<Subtask> getEpicSubtasks(Epic epic);
}
