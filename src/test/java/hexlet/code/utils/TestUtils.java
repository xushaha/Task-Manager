package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class TestUtils {
    public static final String TEST_EMAIL = "email@email.com";
    public static final String TEST_EMAIL_2 = "email2@email.com";
    public static final String ID = "/{id}";
    public static final String BASE_URL = "/api";
    public static final String BASE_USER_URL = BASE_URL + USER_CONTROLLER_PATH;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
        @Autowired
    private JWTHelper jwtHelper;
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

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
        //postCommentRepository.deleteAll();
        //postRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
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

