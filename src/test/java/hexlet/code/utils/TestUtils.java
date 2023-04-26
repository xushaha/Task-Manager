package hexlet.code.utils;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;

import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class TestUtils {
    public static final String ID = "/{id}";
    public static final String BASE_URL = "/api";
    public static final String BASE_USER_URL = BASE_URL + USER_CONTROLLER_PATH;
    public static final String BASE_LABEL_URL = BASE_URL + LABEL_CONTROLLER_PATH;
    public static final String BASE_STATUS_URL = BASE_URL + TASK_STATUS_CONTROLLER_PATH;
    public static final String BASE_TASK_URL = BASE_URL + TASK_CONTROLLER_PATH;
    public static final String TEST_EMAIL = "email@email.com";
    public static final String TEST_EMAIL_2 = "email2@email.com";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private JWTHelper jwtHelper;

    private final UserDto testRegistrationDto = new UserDto(
            TEST_EMAIL,
            "first name",
            "last name",
            "password"
    );

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public void clearDataBase() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(BASE_USER_URL)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions createTaskStatus(String name) throws Exception {
        TaskStatusDto taskStatusDto = new TaskStatusDto(name);
        return perform(post(BASE_STATUS_URL)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON), TEST_EMAIL);
    }

    public ResultActions createLabel(String name) throws Exception {
        LabelDto labelDto = new LabelDto(name);
        return perform(post(BASE_LABEL_URL)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON), TEST_EMAIL);
    }

    public ResultActions createTask() throws Exception {
        User user = userRepository.findAll().get(0);

        createTaskStatus("new task status");
        TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        createLabel("new label");
        Label label = labelRepository.findAll().get(0);

        TaskDto taskDto = new TaskDto(
                "Task",
                "description",
                taskStatus.getId(),
                user.getId(),
                List.of(label.getId()));

        return perform(post(BASE_TASK_URL)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON), TEST_EMAIL);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

}
