package com.fms.interview_exercise.service;

import com.fms.interview_exercise.domain.User;
import com.fms.interview_exercise.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service class for managing users.
 */
public interface UserService {

    User createUser(String login, String password);

    User createUser(UserDTO userDTO);

    Optional<UserDTO> updateUser(UserDTO userDTO);

    void deleteUser(String login);

    void changePassword(String password);

    Page<UserDTO> getAllManagedUsers(Pageable pageable);

    Optional<User> getUserWithAuthoritiesByLogin(String login);

    User getUserWithAuthorities(Long id);

    User getUserWithAuthorities();

    Optional<User> findOneByLogin(String login);

}
