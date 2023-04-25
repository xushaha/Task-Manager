package hexlet.code.controller;

import hexlet.code.config.SpringConfigForIT;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.clearDataBase();
    }

    @Test
    public void testCreateNewTask() throws Exception {
        assertEquals(0, taskRepository.count());
        utils.createTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());
    }



}
