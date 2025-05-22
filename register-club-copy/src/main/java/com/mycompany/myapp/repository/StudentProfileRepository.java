package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StudentProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentProfile entity.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    default Optional<StudentProfile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentProfile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentProfile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studentProfile from StudentProfile studentProfile left join fetch studentProfile.user",
        countQuery = "select count(studentProfile) from StudentProfile studentProfile"
    )
    Page<StudentProfile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select studentProfile from StudentProfile studentProfile left join fetch studentProfile.user")
    List<StudentProfile> findAllWithToOneRelationships();

    @Query("select studentProfile from StudentProfile studentProfile left join fetch studentProfile.user where studentProfile.id =:id")
    Optional<StudentProfile> findOneWithToOneRelationships(@Param("id") Long id);
}
