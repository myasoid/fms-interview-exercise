package com.fms.interview_exercise.web.rest;


import com.fms.interview_exercise.InterviewExerciseApplication;
import com.fms.interview_exercise.domain.Authority;
import com.fms.interview_exercise.repository.AuthorityRepository;
import com.fms.interview_exercise.service.AuthorityService;
import com.fms.interview_exercise.service.dto.AuthorityDTO;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AuthorityResource REST controller.
 *
 * @see AuthorityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InterviewExerciseApplication.class)
public class AuthorityResourceIntTest {

    private static final String DEFAULT_AUTHORITY_NAME = "AAA";
    private static final String UPDATED_AUTHORITY_NAME = "BBB";

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAuthorityMockMvc;

    private Authority authority;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Authority createEntity() {
        Authority authority = new Authority();
        authority.setName(DEFAULT_AUTHORITY_NAME);
        return authority;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuthorityResource authorityResource = new AuthorityResource(authorityService);
        this.restAuthorityMockMvc = MockMvcBuilders.standaloneSetup(authorityResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        authority = createEntity();
    }

    @Test
    @Transactional
    public void createAuthority() throws Exception {
        int databaseSizeBeforeCreate = authorityRepository.findAll().size();

        // Create the Authority
        AuthorityDTO authorityDTO = authorityToAuthorityDTO(authority);

        restAuthorityMockMvc.perform(post("/api/authorities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
                .andExpect(status().isCreated());

        // Validate the Authority in the database
        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeCreate + 1);
        Authority testAuthority = authorityList.get(authorityList.size() - 1);
        assertThat(testAuthority.getName()).isEqualTo(DEFAULT_AUTHORITY_NAME);
    }

    @Test
    @Transactional
    public void createAuthorityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = authorityRepository.findAll().size();

        // Create the Authority with an existing ID
        Authority existingAuthority = new Authority();
        existingAuthority.setId(1L);
        AuthorityDTO existingAuthorityDTO = authorityToAuthorityDTO(existingAuthority);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorityMockMvc.perform(post("/api/authorities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(existingAuthorityDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAuthorityNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = authorityRepository.findAll().size();
        // set the field null
        authority.setName(null);

        // Create the Authority, which fails.
        AuthorityDTO authorityDTO = authorityToAuthorityDTO(authority);

        restAuthorityMockMvc.perform(post("/api/authorities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
                .andExpect(status().isBadRequest());

        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAuthorities() throws Exception {
        // Initialize the database
        authorityRepository.saveAndFlush(authority);

        // Get all the authorityList
        restAuthorityMockMvc.perform(get("/api/authorities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(authority.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_AUTHORITY_NAME.toString())));
    }

    @Test
    @Transactional
    public void getAuthority() throws Exception {
        // Initialize the database
        authorityRepository.saveAndFlush(authority);

        // Get the authority
        restAuthorityMockMvc.perform(get("/api/authorities/{id}", authority.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(authority.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_AUTHORITY_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAuthority() throws Exception {
        // Get the authority
        restAuthorityMockMvc.perform(get("/api/authorities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuthority() throws Exception {
        // Initialize the database
        authorityRepository.saveAndFlush(authority);
        int databaseSizeBeforeUpdate = authorityRepository.findAll().size();

        // Update the authority
        Authority updatedAuthority = authorityRepository.findOne(authority.getId());
        updatedAuthority.setName(UPDATED_AUTHORITY_NAME);
        AuthorityDTO authorityDTO = authorityToAuthorityDTO(updatedAuthority);

        restAuthorityMockMvc.perform(put("/api/authorities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
                .andExpect(status().isOk());

        // Validate the Authority in the database
        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeUpdate);
        Authority testAuthority = authorityList.get(authorityList.size() - 1);
        assertThat(testAuthority.getName()).isEqualTo(UPDATED_AUTHORITY_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingAuthority() throws Exception {
        int databaseSizeBeforeUpdate = authorityRepository.findAll().size();

        // Create the Authority
        AuthorityDTO authorityDTO = authorityToAuthorityDTO(authority);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAuthorityMockMvc.perform(put("/api/authorities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
                .andExpect(status().isCreated());

        // Validate the Authority in the database
        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAuthority() throws Exception {
        // Initialize the database
        authorityRepository.saveAndFlush(authority);
        int databaseSizeBeforeDelete = authorityRepository.findAll().size();

        // Get the authority
        restAuthorityMockMvc.perform(delete("/api/authorities/{id}", authority.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Authority> authorityList = authorityRepository.findAll();
        assertThat(authorityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private Authority authorityDTOToAuthority(AuthorityDTO authorityDTO) {
        Authority result = new Authority();
        result.setName(authorityDTO.getName());
        result.setId(authorityDTO.getId());
        return result;
    }

    private AuthorityDTO authorityToAuthorityDTO(Authority authority) {
        AuthorityDTO result = new AuthorityDTO();
        result.setName(authority.getName());
        result.setId(authority.getId());
        return result;
    }
}
