package ru.evgenii.kanban.exeptions;

public class ValidationIntersectionException extends RuntimeException{

    public ValidationIntersectionException(String message) {
        super(message);
    }
}
