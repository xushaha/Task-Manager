package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.LabelRepository;
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
import java.util.Set;

import static hexlet.code.utils.TestUtils.BASE_TASK_URL;
import static hexlet.code.utils.TestUtils.ID;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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


    @Test
    public void testGetLabelById() throws Exception {
        utils.createTask().andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);
        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = utils.perform(
                        get(BASE_TASK_URL + ID, expectedTask.getId()),
                        expectedTask.getName()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getAuthor().getId(), task.getAuthor().getId());
        assertEquals(expectedTask.getCreatedAt().getTime(), task.getCreatedAt().getTime());
    }



    @Test
    public void testGetAllLabels() throws Exception {
        utils.createTask().andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);

        final var response = utils.perform(get(BASE_TASK_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response
                .getContentAsString(), new TypeReference<>() {
                });

        assertThat(tasks).hasSize(1);
    }


    @Test
    public void testUpdateTask() throws Exception {
        utils.createTask().andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);

        Task task = taskRepository.findAll().get(0);
        Label label = labelRepository.findAll().get(0);

        TaskDto taskDto = new TaskDto(
                "Updated task",
                "Updated description",
                task.getTaskStatus().getId(),
                task.getExecutor().getId(),
                Set.of(label.getId()));

        utils.perform(put(BASE_TASK_URL + ID, task.getId())
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andExpect(status().isOk());

        task = taskRepository.findAll().get(0);
        assertEquals(1, taskRepository.count());
        assertEquals("Updated task", task.getName());
        assertEquals("Updated description", task.getDescription());
    }


    @Test
    public void deleteTask() throws Exception {
        utils.createTask().andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);

        Long taskId = taskRepository.findAll().get(0).getId();

        utils.perform(delete(BASE_TASK_URL + ID, taskId), TEST_EMAIL)
                .andExpect(status().isOk());
        assertThat(taskRepository.count()).isEqualTo(0);

    }


    @Test
    public void deleteTaskFails() throws Exception {
        utils.createTask().andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);

        Long taskId = taskRepository.findAll().get(0).getId() + 1;

        var request = delete(BASE_TASK_URL + ID, taskId);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isNotFound());

        assertEquals(1, labelRepository.count());
    }

}
