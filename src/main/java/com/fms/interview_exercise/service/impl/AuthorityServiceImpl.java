package com.fms.interview_exercise.service.impl;

import com.fms.interview_exercise.domain.Authority;
import com.fms.interview_exercise.repository.AuthorityRepository;
import com.fms.interview_exercise.service.AuthorityService;
import com.fms.interview_exercise.service.dto.AuthorityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing authorities.
 */
@Service
@Transactional
public class AuthorityServiceImpl implements AuthorityService {

    private final Logger log = LoggerFactory.getLogger(AuthorityServiceImpl.class);

    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    /**
     * Save a authority.
     *
     * @param authorityDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public AuthorityDTO save(AuthorityDTO authorityDTO) {
        log.debug("Request to save Authority : {}", authorityDTO);
        Authority authority = authorityDTOToAuthority(authorityDTO);
        authority = authorityRepository.save(authority);
        AuthorityDTO result = authorityToAuthorityDTO(authority);
        return result;
    }

    /**
     * Get all the authorities.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Authorities");
        Page<Authority> result = authorityRepository.findAll(pageable);
        return result.map(authority -> authorityToAuthorityDTO(authority));
    }

    /**
     * Get one authority by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public AuthorityDTO findOne(Long id) {
        log.debug("Request to get Authority : {}", id);
        Authority authority = authorityRepository.findOne(id);
        AuthorityDTO authoritytDTO = authorityToAuthorityDTO(authority);
        return authoritytDTO;
    }

    /**
     * Delete the  authority by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Authority : {}", id);
        authorityRepository.delete(id);
    }

    private Authority authorityDTOToAuthority(AuthorityDTO authorityDTO) {
        if(authorityDTO == null){
            return null;
        }
        Authority result = new Authority();
        result.setName(authorityDTO.getName());
        result.setId(authorityDTO.getId());
        return result;
    }

    private AuthorityDTO authorityToAuthorityDTO(Authority authority) {
        if(authority == null){
            return null;
        }
        AuthorityDTO result = new AuthorityDTO();
        result.setName(authority.getName());
        result.setId(authority.getId());
        return result;
    }
}
