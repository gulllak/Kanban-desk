package ru.evgenii.kanban.exeptions;

public class RequestFailedException extends RuntimeException{
    public RequestFailedException(String message) {
        super(message);
    }
}
