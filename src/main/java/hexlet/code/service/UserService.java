package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    User createNewUser(UserDto userDto);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(long id, UserDto userDto);

    void deleteUser(Long id);

    String getCurrentUserName();

    User getCurrentUser();

}
