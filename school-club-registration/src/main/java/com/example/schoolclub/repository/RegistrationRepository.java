package com.example.schoolclub.repository;

import com.example.schoolclub.domain.Registration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Registration entity.
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    @Query("select registration from Registration registration where registration.user.login = ?#{authentication.name}")
    List<Registration> findByUserIsCurrentUser();

    default Optional<Registration> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Registration> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Registration> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select registration from Registration registration left join fetch registration.user",
        countQuery = "select count(registration) from Registration registration"
    )
    Page<Registration> findAllWithToOneRelationships(Pageable pageable);

    @Query("select registration from Registration registration left join fetch registration.user")
    List<Registration> findAllWithToOneRelationships();

    @Query("select registration from Registration registration left join fetch registration.user where registration.id =:id")
    Optional<Registration> findOneWithToOneRelationships(@Param("id") Long id);
}
