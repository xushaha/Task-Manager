package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;

import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static hexlet.code.controller.UserController.ID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.BASE_USER_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.clearDataBase();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, userRepository.count());
        utils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }


    @Test
    public void getUserById() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var response = utils.perform(
                        get(BASE_USER_URL + ID, expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }


    @Test
    public void getAllUsers() throws Exception {
        utils.regDefaultUser();
        utils.regUser(new UserDto(
                TestUtils.TEST_EMAIL_2,
                "first name",
                "last name",
                "password"
        ));
        final var response = utils.perform(get(BASE_USER_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(2);
    }



/*    @Test
    public void getUserByIdFails() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        utils.perform(get(BASE_USER_URL + ID, expectedUser.getId()))
                .andExpect(status().isUnauthorized());
    }*/




    @Test
    public void updateUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TestUtils.TEST_EMAIL).get().getId();

        final var userDto = new UserDto(TestUtils.TEST_EMAIL_2, "new first name",
                "new last name", "new password");

        final var updateRequest = put(BASE_USER_URL + ID, userId)
                .content(TestUtils.asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TestUtils.TEST_EMAIL).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TestUtils.TEST_EMAIL).orElse(null));
        assertNotNull(userRepository.findByEmail(TestUtils.TEST_EMAIL_2).orElse(null));
    }


    @Test
    public void twiceRegTheSameUserFail() throws Exception {
        utils.regDefaultUser().andExpect(status().isCreated());
        utils.regDefaultUser().andExpect(status().isUnprocessableEntity());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void login() throws Exception {
        utils.regDefaultUser();
        final LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = post(TestUtils.BASE_URL + LOGIN)
                .content(TestUtils.asJson(loginDto))
                .contentType(APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isOk());
    }


    @Test
    public void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = post(TestUtils.BASE_URL + LOGIN)
                .content(TestUtils.asJson(loginDto))
                .contentType(APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }


    @Test
    public void deleteUser() throws Exception {
        utils.regDefaultUser();
        final Long userId = userRepository.findByEmail(TestUtils.TEST_EMAIL).get().getId();
        utils.perform(delete(BASE_USER_URL + ID, userId), TestUtils.TEST_EMAIL)
                .andExpect(status().isOk());
        assertEquals(0, userRepository.count());

    }

    @Test
    public void deleteUserFails() throws Exception {
        utils.regDefaultUser();
        utils.regUser(new UserDto(
                TestUtils.TEST_EMAIL_2,
                "first name",
                "last name",
                "password"
        ));

        final Long userId = userRepository.findByEmail(TestUtils.TEST_EMAIL).get().getId();

        utils.perform(delete(BASE_USER_URL + ID, userId), TestUtils.TEST_EMAIL_2)
                .andExpect(status().isForbidden());

        assertEquals(2, userRepository.count());

    }
}
