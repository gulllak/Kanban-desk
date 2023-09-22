package ru.evgenii.kanban.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.evgenii.kanban.models.Epic;
import ru.evgenii.kanban.models.Subtask;
import ru.evgenii.kanban.models.Task;
import ru.evgenii.kanban.services.TaskManager;
import ru.evgenii.kanban.utils.Endpoint;
import ru.evgenii.kanban.utils.TaskTypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::tasksHandler);
        httpServer.start();
        this.taskManager = taskManager;
        gson = TaskTypeAdapter.getGson();
    }

    private void tasksHandler(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String url = httpExchange.getRequestURI().toString();
        String[] parts = url.split("/");

        Endpoint endpoint = getEndpoint(url, requestMethod);
        switch (endpoint) {
            case GET_ALL:
                handleGetPrioritizedTasks(httpExchange);
                break;
            case GET_TASKS:
                handleGetTasks(httpExchange, url);
                break;
            case GET_BY_ID:
                handleGetTaskById(httpExchange, parts);
                break;
            case GET_HISTORY:
                handleGetHistory(httpExchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(httpExchange, url);
                break;
            case DELETE_BY_ID:
                handleDeleteTaskById(httpExchange, parts);
                break;
            case POST_TASK:
                handlePostTask(httpExchange, url);
                break;
            case UNKNOWN:
                httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
        }
    }

    private void handlePostTask(HttpExchange httpExchange, String url) throws IOException {
        InputStream io = httpExchange.getRequestBody();
        String json = new String(io.readAllBytes(), StandardCharsets.UTF_8);

        if(json.isEmpty()) {
            writeResponse(httpExchange, "", 400);
            return;
        }

        if (url.contains("/tasks/task")) {
            Task task = gson.fromJson(json, Task.class);

            boolean isUpdate = taskManager.getAllTask().stream().anyMatch(s -> s.getId() == task.getId());
            if (isUpdate) {
                taskManager.updateTask(task);
            } else {
                taskManager.addTask(task);
            }
            writeResponse(httpExchange, gson.toJson("OK"), 200);
        }
        if (url.contains("/tasks/subtask")) {
            Subtask subtask = gson.fromJson(json, Subtask.class);

            Optional<Epic> epic = taskManager.getAllEpic().stream().filter(s -> s.getId() == subtask.getEpic().getId()).findFirst();
            if(epic.isEmpty()){
                writeResponse(httpExchange, "", 400);
                return;
            } else {
                subtask.setEpic(epic.get());
            }

            boolean isUpdate = taskManager.getAllSubtask().stream().anyMatch(s -> s.getId() == subtask.getId());

            if (isUpdate) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.addSubtask(subtask);
            }
            writeResponse(httpExchange, gson.toJson("OK"), 200);
        }
        if (url.contains("/tasks/epic")) {
            Epic epic = gson.fromJson(json, Epic.class);

            try {
                List<Subtask> createSubtasks = new ArrayList<>();
                if (!epic.getSubtasks().isEmpty()) {
                    List<Subtask> subtasks = taskManager.getAllSubtask();
                    for (Subtask tempSubtask : epic.getSubtasks()) {
                        for (Subtask realSubtask : subtasks) {
                            if (tempSubtask.getId() == realSubtask.getId()) {
                                createSubtasks.add(realSubtask);
                            }
                        }
                    }
                    if (createSubtasks.size() != epic.getSubtasks().size()) {
                        writeResponse(httpExchange, "", 400);
                        return;
                    }
                }
                epic.setSubtasks(createSubtasks);
            } catch (NullPointerException e) {
                writeResponse(httpExchange, "", 400);
                return;
            }

            boolean isUpdate = taskManager.getAllEpic().stream().anyMatch(s -> s.getId() == epic.getId());

            if (isUpdate) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.addEpic(epic);
            }
            writeResponse(httpExchange, gson.toJson("OK"), 200);
        }
    }

    private void handleDeleteTaskById(HttpExchange httpExchange, String[] parts) throws IOException {
        int id = Integer.parseInt(parts[parts.length-1].replaceAll("[^\\d]", ""));

        if (parts[2].equals("task")) {
            try {
                taskManager.deleteTaskById(id);
            } catch (NoSuchElementException e) {
                writeResponse(httpExchange, "", 400);
                return;
            }
            writeResponse(httpExchange, "", 200);
        }
        if (parts[2].equals("subtask")) {
            try {
                taskManager.deleteSubtaskById(id);
            } catch (NoSuchElementException e) {
                writeResponse(httpExchange, "", 400);
                return;
            }
            writeResponse(httpExchange, "", 200);
        }
        if (parts[2].equals("epic")) {
            try {
                taskManager.deleteEpicById(id);
            } catch (NoSuchElementException e) {
                writeResponse(httpExchange, "", 400);
                return;
            }
            writeResponse(httpExchange, "", 200);
        }
    }

    private void handleDeleteTasks(HttpExchange httpExchange, String url) throws IOException {
        if(url.contains("/tasks/task")) {
            taskManager.deleteAllTask();
            writeResponse(httpExchange, "", 200);
        }
        if(url.contains("/tasks/subtask")) {
            taskManager.deleteAllSubtask();
            writeResponse(httpExchange, "", 200);
        }
        if(url.contains("/tasks/epic")) {
            taskManager.deleteAllEpic();
            writeResponse(httpExchange, "", 200);
        }
    }

    private void handleGetTaskById(HttpExchange httpExchange, String[] parts) throws IOException {
        int id = Integer.parseInt(parts[parts.length-1].replaceAll("[^\\d]", ""));

        if(parts.length == 4) {
            if (parts[2].equals("task")) {
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    writeResponse(httpExchange, gson.toJson(task), 200);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
            }
            if (parts[2].equals("subtask")) {
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    writeResponse(httpExchange, gson.toJson(subtask), 200);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
            }
            if (parts[2].equals("epic")) {
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    writeResponse(httpExchange, gson.toJson(epic), 200);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
            }
        } else if (parts.length == 5 & parts[2].equals("subtask") & parts[3].equals("epic")) {
            try {
                List<Subtask> list = taskManager.getAllSubtasksByEpicId(id);
                if(!list.isEmpty()){
                    writeResponse(httpExchange, gson.toJson(list), 200);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
            } catch (NoSuchElementException e) {
                writeResponse(httpExchange, "", 404);
            }

        }
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        List<Task> list = taskManager.getHistoryManager().getHistory();
        writeResponse(httpExchange, gson.toJson(list), 200);
    }

    private void handleGetTasks(HttpExchange httpExchange, String url) throws IOException {
        if(url.contains("/tasks/task")) {
            List<Task> list = taskManager.getAllTask();
            if(!list.isEmpty()){
                writeResponse(httpExchange, gson.toJson(list), 200);
            } else {
                writeResponse(httpExchange, "", 404);
            }
        }
        if(url.contains("/tasks/subtask")) {
            List<Subtask> list = taskManager.getAllSubtask();
            if(!list.isEmpty()) {
                writeResponse(httpExchange, gson.toJson(list), 200);
            } else {
                writeResponse(httpExchange, "", 404);
            }
        }
        if(url.contains("/tasks/epic")) {
            List<Epic> list = taskManager.getAllEpic();
            if(!list.isEmpty()) {
                writeResponse(httpExchange, gson.toJson(list), 200);
            } else {
                writeResponse(httpExchange, "", 404);
            }
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange httpExchange) {
        List<Task> list = taskManager.getPrioritizedTasks();
        try {
            if(!list.isEmpty()) {
                writeResponse(httpExchange, gson.toJson(list), 200);
            } else {
                writeResponse(httpExchange, "", 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");
        switch (requestMethod) {
            case "GET":
                switch (path) {
                    case "/tasks":
                        return Endpoint.GET_ALL;
                    case "/tasks/history":
                        return Endpoint.GET_HISTORY;
                    case "/tasks/task":
                    case "/tasks/subtask":
                    case "/tasks/epic":
                        return Endpoint.GET_TASKS;
                }
                if (pathParts[pathParts.length-1].startsWith("?id") & (pathParts[2].equals("task") || pathParts[2].equals("subtask") || pathParts[2].equals("epic"))) {
                    return Endpoint.GET_BY_ID;
                }
            case "POST":
                if(pathParts.length == 3 & pathParts[1].equals("tasks")) {
                    return Endpoint.POST_TASK;
                }

            case "DELETE":
                switch (path) {
                    case "/tasks/task":
                    case "/tasks/subtask":
                    case "/tasks/epic":
                        return Endpoint.DELETE_TASKS;
                }
                if(pathParts[pathParts.length-1].startsWith("?id") & (pathParts[2].equals("task") || pathParts[2].equals("subtask") || pathParts[2].equals("epic"))) {
                    return Endpoint.DELETE_BY_ID;
                }
            default: return Endpoint.UNKNOWN;
        }
    }
    private void writeResponse(HttpExchange httpExchange,
                               String responseString,
                               int responseCode) throws IOException {
        if(responseString.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            httpExchange.close();
        } else {
            byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(responseCode, bytes.length);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    public void stop() {
        httpServer.stop(0);
    }
}
