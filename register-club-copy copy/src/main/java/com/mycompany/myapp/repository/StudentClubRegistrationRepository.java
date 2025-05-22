package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StudentClubRegistration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentClubRegistration entity.
 */
@Repository
public interface StudentClubRegistrationRepository extends JpaRepository<StudentClubRegistration, Long> {
    default Optional<StudentClubRegistration> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentClubRegistration> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentClubRegistration> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studentClubRegistration from StudentClubRegistration studentClubRegistration left join fetch studentClubRegistration.club",
        countQuery = "select count(studentClubRegistration) from StudentClubRegistration studentClubRegistration"
    )
    Page<StudentClubRegistration> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select studentClubRegistration from StudentClubRegistration studentClubRegistration left join fetch studentClubRegistration.club"
    )
    List<StudentClubRegistration> findAllWithToOneRelationships();

    @Query(
        "select studentClubRegistration from StudentClubRegistration studentClubRegistration left join fetch studentClubRegistration.club where studentClubRegistration.id =:id"
    )
    Optional<StudentClubRegistration> findOneWithToOneRelationships(@Param("id") Long id);
}
