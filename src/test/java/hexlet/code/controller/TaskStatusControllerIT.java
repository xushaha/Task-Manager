package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.BASE_STATUS_URL;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
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
    public void testCreateNewTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        utils.createTaskStatus("new task status").andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void twiceCreateTaskStatusFail() throws Exception {
        utils.createTaskStatus("new task status").andExpect(status().isCreated());
        utils.createTaskStatus("new task status").andExpect(status().isUnprocessableEntity());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void testGetTaskStatusById() throws Exception {
        utils.createTaskStatus("new task status");
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.perform(
                        get(BASE_STATUS_URL + ID, expectedTaskStatus.getId()),
                        expectedTaskStatus.getName()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void testGetAllTaskStatuses() throws Exception {
        utils.createTaskStatus("new task status");
        utils.createTaskStatus("new task status 2");

        final var response = utils.perform(get(BASE_STATUS_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = utils.fromJson(response
                .getContentAsString(), new TypeReference<>() {
                });

        assertThat(taskStatuses).hasSize(2);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        utils.createTaskStatus("new task status");

        final var response = utils.perform(get(BASE_STATUS_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);
        TaskStatusDto newTaskStatusDto = new TaskStatusDto("updated task status");
        final var responsePut = utils.perform(
                        put(BASE_STATUS_URL + ID, expectedStatus.getId())
                                .content(TestUtils.asJson(newTaskStatusDto))
                                .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andReturn().getResponse();

        TaskStatus expected = TestUtils.fromJson(responsePut.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(taskStatusRepository.existsById(expected.getId()));
        assertThat(expected.getName()).isEqualTo("updated task status");

    }

    @Test
    public void deleteTaskStatus() throws Exception {
        assertThat(taskStatusRepository.count()).isEqualTo(0);

        utils.createTaskStatus("new task status");
        assertThat(taskStatusRepository.count()).isEqualTo(1);

        TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        utils.perform(delete(BASE_STATUS_URL + ID, taskStatus.getId()), TEST_EMAIL)
                .andExpect(status().isOk());
        assertThat(taskStatusRepository.count()).isEqualTo(0);

    }
}
