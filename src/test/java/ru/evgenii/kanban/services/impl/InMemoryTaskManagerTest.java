package ru.evgenii.kanban.services.impl;

import org.junit.jupiter.api.BeforeEach;
import ru.evgenii.kanban.services.Managers;
import ru.evgenii.kanban.services.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initInMemory() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }

}