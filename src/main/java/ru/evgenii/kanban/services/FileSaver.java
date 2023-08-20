package ru.evgenii.kanban.services;

import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.utils.TaskType;

import java.util.stream.Collectors;

public class FileSaver {
    private static final String DELIMITER = ",";
    public String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(DELIMITER)
                .append(task.getTaskType()).append(DELIMITER)
                .append(task.getName()).append(DELIMITER)
                .append(task.getStatus()).append(DELIMITER)
                .append(task.getDescription()).append(DELIMITER);

        if(task.getTaskType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpic().getId());
        }
        return sb.toString();
    }
    public String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(Task::getId).collect(Collectors.toList()).stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
