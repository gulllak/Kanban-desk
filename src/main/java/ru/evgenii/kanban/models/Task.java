package ru.evgenii.kanban.models;

import ru.evgenii.kanban.utils.TaskStatus;
import ru.evgenii.kanban.utils.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;

    protected String name;
    protected String description;
    protected TaskStatus status;

    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.startTime = null;
        this.duration = Duration.ofMinutes(0);
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }
    public Task(String name, String description, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime);
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }
    public Task(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.startTime = null;
        this.duration = Duration.ofMinutes(0);
        this.status = TaskStatus.NEW;
        this.taskType = taskType;
    }
    public Task(String name, String description, TaskType taskType, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime);
        this.duration = Duration.ofMinutes(duration);
        this.status = TaskStatus.NEW;
        this.taskType = taskType;
    }
    public Task(int id, String name, String description, TaskStatus status, TaskType taskType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = null;
        this.duration = Duration.ofMinutes(0);
        this.status = status;
        this.taskType = taskType;
    }
    public Task(int id, String name, String description, TaskStatus status, TaskType taskType, String startTime, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime);
        this.duration = Duration.ofMinutes(duration);
        this.status = status;
        this.taskType = taskType;
    }

    public Task(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && taskType == task.taskType && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, taskType, duration, startTime);
    }
}
