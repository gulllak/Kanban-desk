package services.impl;

import models.Epic;
import models.Subtask;
import models.Task;
import services.HistoryManager;
import services.Managers;
import services.TaskManager;
import utils.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idInc = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            if(!subtask.getEpic().getSubtasks().isEmpty()) {
                subtask.getEpic().getSubtasks().clear();
                subtask.getEpic().setStatus(TaskStatus.NEW);
            }
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task addTask(Task task) {
        task.setId(++idInc);
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(++idInc);
        subtask.getEpic().getSubtasks().add(subtask);
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(subtask.getEpic());

        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(++idInc);
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        if(tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);

            return task;
        } else {
            return null;
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if(subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);

            updateEpicStatus(subtask.getEpic());
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if(epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);

            return epic;
        } else {
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.get(id).getEpic().getSubtasks().remove(subtask);
        subtasks.remove(id);

        updateEpicStatus(subtask.getEpic());
    }

    @Override
    public void deleteEpicById(int id) {
        subtasks.values().removeAll(epics.get(id).getSubtasks());
        epics.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    private void updateEpicStatus(Epic epic) {
        if(!epic.getSubtasks().isEmpty()) {
            Set<TaskStatus> statuses = epic.getSubtasks().stream()
                    .map(Task::getStatus)
                    .collect(Collectors.toSet());

            epic.setStatus(statuses.size() == 1 ? statuses.stream().findFirst().get() : TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}
