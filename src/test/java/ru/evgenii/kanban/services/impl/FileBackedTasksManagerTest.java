package ru.evgenii.kanban.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.evgenii.kanban.exeptions.ManagerSaveException;
import ru.evgenii.kanban.services.TaskManagerTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void initInMemory() {
        Path path = Paths.get("src/test/resources/testBackup.csv");
        try {
            Files.writeString(path, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = new FileBackedTasksManager(path.toFile());
    }

    @Test
    void catchManagerSaveExceptionWithIncorrectPath(){
        ManagerSaveException elementException = assertThrows(ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager = new FileBackedTasksManager(Paths.get("src/test/resource/testackup.csv").toFile());
                        taskManager.addTask(task1);
                    }
                });
    }

    @Test
    void saveTask() {
        assertEquals(0, taskManager.getAllTask().size(), "Список не пустой");
        taskManager.addTask(null);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());
        assertEquals(0, fileBackedTasksManager.getAllTask().size());
        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size());

        taskManager.addTask(task1);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get("src/test/resources/testBackup.csv").toFile()))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                assertEquals("1,TASK,Помыть кота,NEW,Берем кота и моем,2023-09-01T17:00,10", line, "Запись неверная");
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveEpic() {
        assertEquals(0, taskManager.getAllEpic().size(), "Список не пустой");
        taskManager.addEpic(null);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());
        assertEquals(0, fileBackedTasksManager.getAllEpic().size());
        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size());

        taskManager.addEpic(epic);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get("src/test/resources/testBackup.csv").toFile()))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                assertEquals("1,EPIC,Написать код программы,NEW,Янедкс Практикум,", line, "Запись неверная");
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size());

        taskManager.getEpicById(1);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get("src/test/resources/testBackup.csv").toFile()))) {
            bufferedReader.readLine();
            bufferedReader.readLine();
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                assertEquals("1", line, "История неверная");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromFile() {
        taskManager.addTask(task1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());

        assertEquals(taskManager.getHistoryManager().getHistory(), fileBackedTasksManager.getHistoryManager().getHistory(), "История не совпадает");

        assertEquals(task1, fileBackedTasksManager.getTaskById(1), "Таски не совпадают");
        assertEquals(epic, fileBackedTasksManager.getEpicById(2), "Эпики не совпадают");
        assertEquals(subtask1, fileBackedTasksManager.getSubtaskById(3), "Подзадачи не совпадают");
    }

    @Test
    void loadFromEmptyFile() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());

        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size(), "История не пустая");

        assertEquals(0, fileBackedTasksManager.getAllTask().size(), "Таски не пустые");
        assertEquals(0, fileBackedTasksManager.getAllEpic().size(), "Эпики не пустые");
        assertEquals(0, fileBackedTasksManager.getAllSubtask().size(), "Подзадачи не пустые");
    }

    @Test
    void loadFromFileWithoutHistory() {
        taskManager.addTask(task1);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());

        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size(), "История не пустая");
        assertEquals(1, fileBackedTasksManager.getAllTask().size(), "Таски не пустые");
    }

    @Test
    void loadFromFileEpicWithoutSubtasks() {
        taskManager.addTask(epic);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("src/test/resources/testBackup.csv").toFile());

        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size(), "История не пустая");
        assertEquals(1, fileBackedTasksManager.getAllEpic().size(), "Таски не пустые");
        assertEquals(epic, fileBackedTasksManager.getEpicById(1));
    }


}
