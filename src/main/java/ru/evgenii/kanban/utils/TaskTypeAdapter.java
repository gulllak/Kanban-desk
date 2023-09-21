package ru.evgenii.kanban.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskTypeAdapter {
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Task.class, new TypeAdapter<Task>() {
            @Override
            public void write(JsonWriter jsonWriter, Task task) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("id").value(task.getId());
                jsonWriter.name("name").value(task.getName());
                jsonWriter.name("description").value(task.getDescription());
                jsonWriter.name("status").value(task.getStatus().toString());
                jsonWriter.name("taskType").value(task.getTaskType().toString());
                jsonWriter.name("duration").value(task.getDuration().toMinutes());

                if(task.getStartTime() != null) {
                    jsonWriter.name("startTime").value(task.getStartTime().toEpochSecond(ZoneOffset.UTC));
                } else {
                    jsonWriter.name("startTime").value("null");
                }
                jsonWriter.endObject();
            }

            @Override
            public Task read(JsonReader jsonReader) throws IOException {
                Task task = new Task("", "");
                jsonReader.beginObject();
                String fieldname = null;

                while (jsonReader.hasNext()) {
                    JsonToken token = jsonReader.peek();
                    if (token.equals(JsonToken.NAME)) {
                        fieldname = jsonReader.nextName();
                    }
                    if("id".equals(fieldname)){
                        token = jsonReader.peek();
                        task.setId(jsonReader.nextInt());
                    }
                    if("name".equals(fieldname)) {
                        token = jsonReader.peek();
                        task.setName(jsonReader.nextString());
                    }
                    if("description".equals(fieldname)){
                        token = jsonReader.peek();
                        task.setDescription(jsonReader.nextString());
                    }
                    if("status".equals(fieldname)) {
                        token = jsonReader.peek();
                        task.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                    }
                    if("taskType".equals(fieldname)) {
                        token = jsonReader.peek();
                        task.setTaskType(TaskType.valueOf(jsonReader.nextString()));
                    }
                    if("duration".equals(fieldname)) {
                        token = jsonReader.peek();
                        task.setDuration(Duration.ofMinutes(jsonReader.nextLong()));
                    }
                    if("startTime".equals(fieldname)) {
                        token = jsonReader.peek();
                        String startTime = jsonReader.nextString();

                        if(startTime.equals("null")){
                            task.setStartTime(null);
                        } else {
                            task.setStartTime(LocalDateTime.ofEpochSecond(Long.parseLong(startTime), 0, ZoneOffset.UTC));
                        }
                    }
                }
                jsonReader.endObject();
                return task;
            }
        });

        gsonBuilder.registerTypeAdapter(Epic.class, new TypeAdapter<Epic>() {
            @Override
            public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("id").value(epic.getId());
                jsonWriter.name("name").value(epic.getName());
                jsonWriter.name("description").value(epic.getDescription());
                jsonWriter.name("status").value(epic.getStatus().toString());
                jsonWriter.name("taskType").value(epic.getTaskType().toString());
                jsonWriter.name("duration").value(epic.getDuration().toMinutes());

                if(epic.getStartTime() != null) {
                    jsonWriter.name("startTime").value(epic.getStartTime().toEpochSecond(ZoneOffset.UTC));
                } else {
                    jsonWriter.name("startTime").value("null");
                }
                jsonWriter.name("epicSubtasks").value(epic.getSubtasks().stream()
                        .map(Task::getId)
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));

                if(epic.getEndTime() != null) {
                    jsonWriter.name("endTime").value(epic.getEndTime().toEpochSecond(ZoneOffset.UTC));
                } else {
                    jsonWriter.name("endTime").value("null");
                }
                jsonWriter.endObject();
            }

            @Override
            public Epic read(JsonReader jsonReader) throws IOException {
                Epic epic = new Epic("", "");
                jsonReader.beginObject();
                String fieldname = null;

                while (jsonReader.hasNext()) {
                    JsonToken token = jsonReader.peek();
                    if(token.equals(JsonToken.NAME)){
                        fieldname = jsonReader.nextName();
                    }

                    if("id".equals(fieldname)){
                        token = jsonReader.peek();
                        epic.setId(jsonReader.nextInt());
                    }
                    if("name".equals(fieldname)) {
                        token = jsonReader.peek();
                        epic.setName(jsonReader.nextString());
                    }
                    if("description".equals(fieldname)){
                        token = jsonReader.peek();
                        epic.setDescription(jsonReader.nextString());
                    }
                    if("status".equals(fieldname)) {
                        token = jsonReader.peek();
                        epic.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                    }
                    if("taskType".equals(fieldname)) {
                        token = jsonReader.peek();
                        epic.setTaskType(TaskType.valueOf(jsonReader.nextString()));
                    }
                    if("duration".equals(fieldname)) {
                        token = jsonReader.peek();
                        epic.setDuration(Duration.ofMinutes(jsonReader.nextLong()));
                    }
                    if("startTime".equals(fieldname)) {
                        token = jsonReader.peek();
                        String startTime = jsonReader.nextString();

                        if(startTime.equals("null")){
                            epic.setStartTime(null);
                        } else {
                            epic.setStartTime(LocalDateTime.ofEpochSecond(Long.parseLong(startTime), 0, ZoneOffset.UTC));
                        }
                    }
                    if("epicSubtasks".equals(fieldname)) {
                        token = jsonReader.peek();
                        String ids = jsonReader.nextString();
                        List<Subtask> createSubtasks = new ArrayList<>();
                        if(!ids.equals("")) {
                            for (String id : ids.split(",")) {
                                createSubtasks.add(new Subtask(Integer.parseInt(id)));
                            }
                        }
                        epic.setSubtasks(createSubtasks);
                    }
                    if("endTime".equals(fieldname)) {
                        token = jsonReader.peek();
                        String endTime = jsonReader.nextString();

                        if(endTime.equals("null")){
                            epic.setEndTime(null);
                        } else {
                            epic.setEndTime(LocalDateTime.ofEpochSecond(Long.parseLong(endTime), 0, ZoneOffset.UTC));
                        }
                    }
                }
                jsonReader.endObject();
                return epic;
            }
        });

        gsonBuilder.registerTypeAdapter(Subtask.class, new TypeAdapter<Subtask>() {
            @Override
            public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("id").value(subtask.getId());
                jsonWriter.name("name").value(subtask.getName());
                jsonWriter.name("description").value(subtask.getDescription());
                jsonWriter.name("status").value(subtask.getStatus().toString());
                jsonWriter.name("taskType").value(subtask.getTaskType().toString());
                jsonWriter.name("duration").value(subtask.getDuration().toMinutes());
                if(subtask.getStartTime() != null) {
                    jsonWriter.name("startTime").value(String.valueOf(subtask.getStartTime().toEpochSecond(ZoneOffset.UTC)));
                } else {
                    jsonWriter.name("startTime").value("null");
                }
                jsonWriter.name("epicId").value(subtask.getEpic().getId());
                jsonWriter.endObject();
            }

            @Override
            public Subtask read(JsonReader jsonReader) throws IOException {
                Subtask subtask = new Subtask("", "", null);
                jsonReader.beginObject();
                String fieldname = null;

                while (jsonReader.hasNext()) {
                    JsonToken token = jsonReader.peek();
                    if(token.equals(JsonToken.NAME)){
                        fieldname = jsonReader.nextName();
                    }

                    if("id".equals(fieldname)){
                        token = jsonReader.peek();
                        subtask.setId(jsonReader.nextInt());
                    }
                    if("name".equals(fieldname)) {
                        token = jsonReader.peek();
                        subtask.setName(jsonReader.nextString());
                    }
                    if("description".equals(fieldname)){
                        token = jsonReader.peek();
                        subtask.setDescription(jsonReader.nextString());
                    }
                    if("status".equals(fieldname)) {
                        token = jsonReader.peek();
                        subtask.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                    }
                    if("taskType".equals(fieldname)) {
                        token = jsonReader.peek();
                        subtask.setTaskType(TaskType.valueOf(jsonReader.nextString()));
                    }
                    if("duration".equals(fieldname)) {
                        token = jsonReader.peek();
                        subtask.setDuration(Duration.ofMinutes(jsonReader.nextLong()));
                    }
                    if("startTime".equals(fieldname)) {
                        token = jsonReader.peek();
                        String startTime = jsonReader.nextString();

                        if(startTime.equals("null")){
                            subtask.setStartTime(null);
                        } else {
                            subtask.setStartTime(LocalDateTime.ofEpochSecond(Long.parseLong(startTime), 0, ZoneOffset.UTC));
                        }
                    }
                    if("epicId".equals(fieldname)) {
                        token = jsonReader.peek();
                        int epicId = jsonReader.nextInt();
                        Epic epic = new Epic(epicId);
                        subtask.setEpic(epic);
                    }
                }
                jsonReader.endObject();
                return subtask;
            }
        });
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }
}
