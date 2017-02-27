package com.fms.interview_exercise.service.dto;

import com.fms.interview_exercise.config.Constants;
import com.fms.interview_exercise.domain.Authority;
import com.fms.interview_exercise.domain.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    private Long id;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    private boolean activated = false;

    private Set<Long> authorities;

    public UserDTO() {
        // Empty constructor needed for MapStruct.
    }

    public UserDTO(User user) {
        this(user.getId(), user.getLogin(), user.getActivated(),
                user.getAuthorities().stream()
                        .map(Authority::getId)
                        .collect(Collectors.toSet()));
    }

    public UserDTO(Long id, String login, boolean activated, Set<Long> authorities) {
        this.id = id;
        this.login = login;
        this.activated = activated;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setAuthorities(Set<Long> authorities) {
        this.authorities = authorities;
    }

    public Set<Long> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "login='" + login + '\'' +
                ", activated=" + activated +
                ", authorities=" + authorities +
                "}";
    }
}
