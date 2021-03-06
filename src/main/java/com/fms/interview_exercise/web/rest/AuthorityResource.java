package com.fms.interview_exercise.web.rest;


import com.fms.interview_exercise.service.AuthorityService;
import com.fms.interview_exercise.service.dto.AuthorityDTO;
import com.fms.interview_exercise.web.rest.util.HeaderUtil;
import com.fms.interview_exercise.web.rest.util.PaginationUtil;
import com.fms.interview_exercise.web.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Authority.
 */
@RestController
@RequestMapping("/api")
public class AuthorityResource {

    private static final String ENTITY_NAME = "authority";
    private final Logger log = LoggerFactory.getLogger(AuthorityResource.class);
    private final AuthorityService authorityService;

    public AuthorityResource(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    /**
     * POST  /authorities : Create a new authority.
     *
     * @param authorityDTO the authorityDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new authorityDTO, or with status 400 (Bad Request) if the authority has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/authorities")
    public ResponseEntity<AuthorityDTO> createAuthority(@Valid @RequestBody AuthorityDTO authorityDTO) throws URISyntaxException {
        log.debug("REST request to save Authority : {}", authorityDTO);
        if (authorityDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new authority cannot already have an ID")).body(null);
        }
        AuthorityDTO result = authorityService.save(authorityDTO);
        return ResponseEntity.created(new URI("/api/authorities/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /authorities : Updates an existing authority.
     *
     * @param authorityDTO the authorityDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated authorityDTO,
     * or with status 400 (Bad Request) if the authorityDTO is not valid,
     * or with status 500 (Internal Server Error) if the authorityDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/authorities")
    public ResponseEntity<AuthorityDTO> updateAuthority(@Valid @RequestBody AuthorityDTO authorityDTO) throws URISyntaxException {
        log.debug("REST request to update Authority : {}", authorityDTO);
        if (authorityDTO.getId() == null) {
            return createAuthority(authorityDTO);
        }
        AuthorityDTO result = authorityService.save(authorityDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, authorityDTO.getId().toString()))
                .body(result);
    }

    /**
     * GET  /authorities : get all the authorities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of authorities in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/authorities")
    public ResponseEntity<List<AuthorityDTO>> getAllAuthoritys(Pageable pageable)
            throws URISyntaxException {
        log.debug("REST request to get a page of Authoritys");
        Page<AuthorityDTO> page = authorityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/authorities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /authorities/:id : get the "id" authority.
     *
     * @param id the id of the authorityDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the authorityDTO, or with status 404 (Not Found)
     */
    @GetMapping("/authorities/{id}")
    public ResponseEntity<AuthorityDTO> getAuthority(@PathVariable Long id) {
        log.debug("REST request to get Authority : {}", id);
        AuthorityDTO authorityDTO = authorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(authorityDTO));
    }

    /**
     * DELETE  /authorities/:id : delete the "id" authority.
     *
     * @param id the id of the authorityDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/authorities/{id}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable Long id) {
        log.debug("REST request to delete Authority : {}", id);
        authorityService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
