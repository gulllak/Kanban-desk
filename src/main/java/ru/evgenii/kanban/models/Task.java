package ru.evgenii.kanban.models;

import ru.evgenii.kanban.utils.TaskStatus;
import ru.evgenii.kanban.utils.TaskType;

import java.util.Objects;

public class Task {
    protected int id;

    protected String name;
    protected String description;
    protected TaskStatus status;

    protected TaskType taskType;
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    public Task(int id, String name, String description, TaskStatus status, TaskType taskType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, taskType);
    }
}
