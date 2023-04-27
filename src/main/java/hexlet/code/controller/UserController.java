package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import hexlet.code.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;


@Tag(name = "User controller")
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private final UserService userService;

    private static final String ONLY_OWNER_BY_ID = """
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """;

    // POST /api/users создание пользователя
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ResponseStatus(CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createNewUser(userDto);
    }


    // GET /api/users получение списка пользователей
    @Operation(summary = "Get list of users")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "List of users was successfully loaded", content =
        @Content(schema = @Schema(implementation = User.class))))
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    // GET /api/users/{id} получение пользователя по идентификатору
    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The user is found"),
        @ApiResponse(responseCode = "404", description = "The user with this id was not found")
    })
    @GetMapping(ID)
    public User getUserById(@PathVariable("id") final Long id) {
        return userService.getUserById(id);
    }

    // PUT /api/users/{id} обновление пользователя
    @Operation(summary = "Update user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "404", description = "User with the id not found")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable("id") final Long id,
                           @RequestBody @Valid final UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    // DELETE /api/users/{id} удаление пользователя
    @Operation(summary = "Delete a user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User with that id is not found")})
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }
}
