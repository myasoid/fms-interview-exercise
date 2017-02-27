package com.fms.interview_exercise.web.rest;

import com.fms.interview_exercise.InterviewExerciseApplication;
import com.fms.interview_exercise.domain.Authority;
import com.fms.interview_exercise.domain.User;
import com.fms.interview_exercise.repository.UserRepository;
import com.fms.interview_exercise.service.UserService;
import com.fms.interview_exercise.service.dto.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InterviewExerciseApplication.class)
public class UserResourceIntTest {

    private static final String DEFAULT_USER_LOGIN = "aaaa";
    private static final String DEFAULT_USER_PASSWORD = "aaaa";
    private static final boolean DEFAULT_USER_ACTIVATED = true;
    private static final boolean UPDATED_USER_ACTIVATED = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUserMockMvc;

    private User user;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity() {
        User user = new User();
        user.setLogin(DEFAULT_USER_LOGIN);
        user.setPassword(DEFAULT_USER_PASSWORD);
        user.setActivated(DEFAULT_USER_ACTIVATED);
        return user;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserResource userResource = new UserResource(userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        user = createEntity();
    }

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        UserDTO userDTO = userToUserDTO(user);

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        Assertions.assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        Assertions.assertThat(testUser.getLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        Assertions.assertThat(testUser.getActivated()).isEqualTo(DEFAULT_USER_ACTIVATED);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the userList
        restUserMockMvc.perform(get("/api/users?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_USER_LOGIN.toString())))
                .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_USER_ACTIVATED)));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc.perform(get("/api/users/{login}", user.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.login").value(DEFAULT_USER_LOGIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        updatedUser.setActivated(UPDATED_USER_ACTIVATED);
        UserDTO userDTO = userToUserDTO(updatedUser);

        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        Assertions.assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        Assertions.assertThat(testUser.getActivated()).isEqualTo(UPDATED_USER_ACTIVATED);
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Get the user
        restUserMockMvc.perform(delete("/api/users/{login}", user.getLogin())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        Assertions.assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private User userDTOToUser(UserDTO userDTO) {
        User result = new User();
        result.setLogin(userDTO.getLogin());
        result.setId(userDTO.getId());
        return result;
    }

    private UserDTO userToUserDTO(User user) {
        UserDTO result = new UserDTO();
        result.setLogin(user.getLogin());
        result.setId(user.getId());
        Set<Long> authorities = new HashSet<>();
        for (Authority authority: user.getAuthorities()             ) {
            authorities.add(authority.getId());
        }
        result.setAuthorities(authorities);
        return result;
    }
}
