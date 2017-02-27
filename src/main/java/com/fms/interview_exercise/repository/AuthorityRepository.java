package com.fms.interview_exercise.repository;

import com.fms.interview_exercise.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findOneByName(String name);

}
