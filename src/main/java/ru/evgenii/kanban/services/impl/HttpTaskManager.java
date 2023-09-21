package ru.evgenii.kanban.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.evgenii.kanban.client.KVTaskClient;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.utils.TaskType;
import ru.evgenii.kanban.utils.TaskTypeAdapter;

import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String TASKS_KEY = "tasks";
    private static final String EPICS_KEY = "epics";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String HISTORY_KEY = "history";
    KVTaskClient client;
    Gson gson;

    public HttpTaskManager(String url) {
        super(Paths.get("src/main/resources/backup.csv").toFile());
        this.client = new KVTaskClient(url);
        gson = TaskTypeAdapter.getGson();
        loadFromKVServer();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getAllTask());
        client.put(TASKS_KEY, jsonTasks);

        String jsonEpics = gson.toJson(getAllEpic());
        client.put(EPICS_KEY, jsonEpics);

        String jsonSubtasks = gson.toJson(getAllSubtask());
        client.put(SUBTASKS_KEY, jsonSubtasks);

        List<Integer> historyIds = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        String jsonHistory = gson.toJson(historyIds);
        client.put(HISTORY_KEY, jsonHistory);
    }

    public void loadFromKVServer() {
        boolean isKVServerEmpty = true;
        Map<Integer, Task> recoveredTasks = new LinkedHashMap<>();
        String tasksJson = client.load(TASKS_KEY);
        if(!tasksJson.equals("null")) {
            tasksJson=tasksJson.substring(1, tasksJson.length() - 1).trim().replaceAll("\\\\", "");
            Type listType = new TypeToken<List<Task>>(){}.getType();

            List<Task> tasks = gson.fromJson(tasksJson, listType);
            tasks.forEach(task -> recoveredTasks.put(task.getId(), task));
            isKVServerEmpty = false;
        }

        String epicsJson = client.load(EPICS_KEY);
        if(!epicsJson.equals("null")) {
            epicsJson = epicsJson.substring(1, epicsJson.length() - 1).trim().replaceAll("\\\\", "");
            Type listType = new TypeToken<List<Epic>>(){}.getType();

            List<Epic> epics = gson.fromJson(epicsJson, listType);
            epics.forEach(epic -> recoveredTasks.put(epic.getId(), epic));
            isKVServerEmpty = false;
        }

        String subtasksJson = client.load(SUBTASKS_KEY);
        if(!subtasksJson.equals("null")) {
            subtasksJson = subtasksJson.substring(1, subtasksJson.length() - 1).trim().replaceAll("\\\\", "");
            Type listType = new TypeToken<List<Subtask>>(){}.getType();

            List<Subtask> subtasks = gson.fromJson(subtasksJson, listType);
            subtasks.forEach(subtask -> recoveredTasks.put(subtask.getId(), subtask));
            isKVServerEmpty = false;
        }

        for (Task task : recoveredTasks.values()) {
            if(task instanceof Epic) {
                List<Subtask> subtasks = ((Epic) task).getSubtasks();
                List<Subtask> realSubtasks = new ArrayList<>();
                for(Subtask subtask : subtasks) {
                    int id = subtask.getId();
                    Subtask realSubtask = (Subtask) recoveredTasks.get(id);
                    realSubtask.setEpic((Epic) task);
                    realSubtasks.add(realSubtask);
                }
                ((Epic) task).setSubtasks(realSubtasks);
            }
        }

        if(!isKVServerEmpty) {
            for (Task task : recoveredTasks.values()) {
                if (task.getId() > this.idInc) {
                    this.idInc = task.getId();
                }
                if (task.getTaskType() == TaskType.TASK) {
                    this.tasks.put(task.getId(), task);
                    this.prioritizedTasks.add(task);
                } else if (task.getTaskType() == TaskType.EPIC) {
                    this.epics.put(task.getId(), (Epic) task);
                } else if (task.getTaskType() == TaskType.SUBTASK) {
                    this.subtasks.put(task.getId(), (Subtask) task);
                    this.prioritizedTasks.add(task);
                    this.updateEpicTime(((Subtask) task).getEpic());
                }
            }

            String historyJson = client.load(HISTORY_KEY);
            if (!historyJson.equals("null")) {
                historyJson = historyJson.substring(1, historyJson.length() - 1).trim();
                Type historyType = new TypeToken<ArrayList<String>>() {}.getType();
                List<String> history = gson.fromJson(historyJson, historyType);

                for (String id : history) {
                    this.historyManager.add(recoveredTasks.get(Integer.parseInt(id)));
                }
            }
        }
    }
}
