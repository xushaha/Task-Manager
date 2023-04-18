package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    User createNewUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    String getCurrentUserName();

    User getCurrentUser();

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
