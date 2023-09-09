package ru.evgenii.kanban.services.impl;

import ru.evgenii.kanban.exeptions.ValidationIntersectionException;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.HistoryManager;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.utils.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idInc = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

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
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
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
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if(!tasks.containsKey(id)) {
            return null;
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if(!subtasks.containsKey(id)) {
            return null;
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if(!epics.containsKey(id)) {
            return null;
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task addTask(Task task) {
        if(task == null) {
            return null;
        }

        task.setId(++idInc);

        validate(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if(subtask == null) {
            return null;
        }
        subtask.setId(++idInc);
        validate(subtask);
        subtask.getEpic().getSubtasks().add(subtask);

        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        updateEpicStatus(subtask.getEpic());
        updateEpicTime(subtask.getEpic());

        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if(epic == null) {
            return null;
        }

        epic.setId(++idInc);
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        if(tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            validate(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);

            return task;
        } else {
            return null;
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if(subtasks.containsKey(subtask.getId())) {

            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            validate(subtask);
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

            updateEpicStatus(subtask.getEpic());
            updateEpicTime(subtask.getEpic());

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
        if(!tasks.containsKey(id)) {
            throw new NoSuchElementException();
        }
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if(!subtasks.containsKey(id)) {
            throw new NoSuchElementException();
        }

        historyManager.remove(id);
        Subtask subtask = subtasks.get(id);
        prioritizedTasks.remove(subtask);
        subtasks.get(id).getEpic().getSubtasks().remove(subtask);
        subtasks.remove(id);

        updateEpicStatus(subtask.getEpic());
        updateEpicTime(subtask.getEpic());
    }

    @Override
    public void deleteEpicById(int id) {
        if(!epics.containsKey(id)) {
            throw new NoSuchElementException();
        }

        epics.get(id).getSubtasks().forEach(subtask -> historyManager.remove(subtask.getId()));
        historyManager.remove(id);

        epics.get(id).getSubtasks().forEach(prioritizedTasks::remove);
        subtasks.values().removeAll(epics.get(id).getSubtasks());
        epics.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    protected void updateEpicTime(Epic epic) {
        LocalDateTime startTime = epic.getSubtasks().stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);
        Duration duration = Duration.ofSeconds(epic.getSubtasks().stream()
                .map(Task::getDuration)
                .mapToLong(Duration::getSeconds)
                .sum());
        LocalDateTime endTime = epic.getSubtasks().stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private void validate(Task task) {
        if(task.getStartTime() == null) {
            return;
        }

        int result = prioritizedTasks.stream()
                .filter(currentTask -> currentTask.getStartTime() != null)
                .map(currentTask -> {
                    if(task.getStartTime().isBefore(currentTask.getEndTime()) && task.getEndTime().isAfter(currentTask.getStartTime())) {
                        return 1;
                    }
                    return 0;
                }).reduce(Integer::sum)
                .orElse(0);

        if(result >= 1) {
            throw new ValidationIntersectionException("Задачи пересекаются");
        }
    }
}
