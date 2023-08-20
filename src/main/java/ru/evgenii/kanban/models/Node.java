package ru.evgenii.kanban.models;

public class Node {
    public Node next;
    public Node prev;
    private final Task task;

    public Node(Task task) {
        this.task = task;
    }
    public Task getTask() {
        return task;
    }
}
