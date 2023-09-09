package ru.evgenii.kanban.services;

import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.impl.InMemoryTaskManager;
import ru.evgenii.kanban.utils.TaskStatus;
import ru.evgenii.kanban.utils.TaskType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Formatter extends InMemoryTaskManager {
    private static final String DELIMITER = ",";
    public String getHeader() {
        return "id,type,name,status,description,epic,startTime,duration";
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(DELIMITER)
                .append(task.getTaskType()).append(DELIMITER)
                .append(task.getName()).append(DELIMITER)
                .append(task.getStatus()).append(DELIMITER)
                .append(task.getDescription()).append(DELIMITER);

        if(task.getTaskType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpic().getId()).append(DELIMITER);
        }

        if(task.getStartTime() != null) {
            sb.append(task.getStartTime().toString()).append(DELIMITER)
                    .append(task.getDuration().getSeconds()/60);
        }
        return sb.toString();
    }
    public Task fromString(String value, Map<Integer, Task> recoveredTasks) {
        Task task = null;

        String[] line = value.split(",");
        switch (line[1].toUpperCase()) {
            case "TASK":
                if(line.length > 5) {
                    task = new Task(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()), line[5], Integer.parseInt(line[6]));
                } else {
                    task = new Task(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()));
                }
                break;
            case "EPIC":
                task = new Epic(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()));
                break;
            case "SUBTASK":
                if(line.length > 6) {
                    task = new Subtask(Integer.parseInt(line[0]),
                            line[2], line[4],
                            TaskStatus.valueOf(line[3].toUpperCase()),
                            TaskType.valueOf(line[1].toUpperCase()),
                            (Epic) recoveredTasks.get(Integer.parseInt(line[5])),
                            line[6],
                            Integer.parseInt(line[7]));
                } else {
                    task = new Subtask(Integer.parseInt(line[0]), line[2], line[4], TaskStatus.valueOf(line[3].toUpperCase()), TaskType.valueOf(line[1].toUpperCase()), (Epic) recoveredTasks.get(Integer.parseInt(line[5])));
                }
                break;
        }
        return task;
    }
    public String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(Task::getId).collect(Collectors.toList()).stream().map(Object::toString).collect(Collectors.joining(","));
    }
    public List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }
}
