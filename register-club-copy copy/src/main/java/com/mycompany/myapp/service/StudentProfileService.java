package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.repository.StudentProfileRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.StudentProfile}.
 */
@Service
@Transactional
public class StudentProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentProfileService.class);

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository) {
        this.studentProfileRepository = studentProfileRepository;
    }

    /**
     * Save a studentProfile.
     *
     * @param studentProfile the entity to save.
     * @return the persisted entity.
     */
    public StudentProfile save(StudentProfile studentProfile) {
        LOG.debug("Request to save StudentProfile : {}", studentProfile);
        return studentProfileRepository.save(studentProfile);
    }

    /**
     * Update a studentProfile.
     *
     * @param studentProfile the entity to save.
     * @return the persisted entity.
     */
    public StudentProfile update(StudentProfile studentProfile) {
        LOG.debug("Request to update StudentProfile : {}", studentProfile);
        return studentProfileRepository.save(studentProfile);
    }

    /**
     * Partially update a studentProfile.
     *
     * @param studentProfile the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StudentProfile> partialUpdate(StudentProfile studentProfile) {
        LOG.debug("Request to partially update StudentProfile : {}", studentProfile);

        return studentProfileRepository
            .findById(studentProfile.getId())
            .map(existingStudentProfile -> {
                if (studentProfile.getStudentId() != null) {
                    existingStudentProfile.setStudentId(studentProfile.getStudentId());
                }
                if (studentProfile.getFullName() != null) {
                    existingStudentProfile.setFullName(studentProfile.getFullName());
                }
                if (studentProfile.getGrade() != null) {
                    existingStudentProfile.setGrade(studentProfile.getGrade());
                }

                return existingStudentProfile;
            })
            .map(studentProfileRepository::save);
    }

    /**
     * Get all the studentProfiles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StudentProfile> findAll() {
        LOG.debug("Request to get all StudentProfiles");
        return studentProfileRepository.findAll();
    }

    /**
     * Get all the studentProfiles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StudentProfile> findAllWithEagerRelationships(Pageable pageable) {
        return studentProfileRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one studentProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StudentProfile> findOne(Long id) {
        LOG.debug("Request to get StudentProfile : {}", id);
        return studentProfileRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the studentProfile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StudentProfile : {}", id);
        studentProfileRepository.deleteById(id);
    }
}
