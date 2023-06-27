package services;

import models.Epic;
import models.Subtask;
import models.Task;
import utils.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskManager {
    private static int idInc = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            if(!subtask.getEpic().getSubtasks().isEmpty()) {
                subtask.getEpic().getSubtasks().clear();
                subtask.getEpic().setStatus(TaskStatus.NEW);
            }
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void addTask(Task task) {
        task.setId(++idInc);
        tasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++idInc);
        subtask.getEpic().getSubtasks().add(subtask);
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(subtask.getEpic());
    }

    public void addEpic(Epic epic) {
        epic.setId(++idInc);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(subtask.getEpic());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.get(id).getEpic().getSubtasks().remove(subtask);
        subtasks.remove(id);

        updateEpicStatus(subtask.getEpic());
    }

    public void deleteEpicById(int id) {
        subtasks.values().removeAll(epics.get(id).getSubtasks());
        epics.remove(id);
    }

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
