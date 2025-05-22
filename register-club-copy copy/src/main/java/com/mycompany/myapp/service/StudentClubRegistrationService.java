package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.StudentClubRegistration;
import com.mycompany.myapp.repository.StudentClubRegistrationRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.StudentClubRegistration}.
 */
@Service
@Transactional
public class StudentClubRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentClubRegistrationService.class);

    private final StudentClubRegistrationRepository studentClubRegistrationRepository;

    public StudentClubRegistrationService(StudentClubRegistrationRepository studentClubRegistrationRepository) {
        this.studentClubRegistrationRepository = studentClubRegistrationRepository;
    }

    /**
     * Save a studentClubRegistration.
     *
     * @param studentClubRegistration the entity to save.
     * @return the persisted entity.
     */
    public StudentClubRegistration save(StudentClubRegistration studentClubRegistration) {
        LOG.debug("Request to save StudentClubRegistration : {}", studentClubRegistration);
        return studentClubRegistrationRepository.save(studentClubRegistration);
    }

    /**
     * Update a studentClubRegistration.
     *
     * @param studentClubRegistration the entity to save.
     * @return the persisted entity.
     */
    public StudentClubRegistration update(StudentClubRegistration studentClubRegistration) {
        LOG.debug("Request to update StudentClubRegistration : {}", studentClubRegistration);
        return studentClubRegistrationRepository.save(studentClubRegistration);
    }

    /**
     * Partially update a studentClubRegistration.
     *
     * @param studentClubRegistration the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StudentClubRegistration> partialUpdate(StudentClubRegistration studentClubRegistration) {
        LOG.debug("Request to partially update StudentClubRegistration : {}", studentClubRegistration);

        return studentClubRegistrationRepository
            .findById(studentClubRegistration.getId())
            .map(existingStudentClubRegistration -> {
                if (studentClubRegistration.getRegistrationDate() != null) {
                    existingStudentClubRegistration.setRegistrationDate(studentClubRegistration.getRegistrationDate());
                }
                if (studentClubRegistration.getStatus() != null) {
                    existingStudentClubRegistration.setStatus(studentClubRegistration.getStatus());
                }

                return existingStudentClubRegistration;
            })
            .map(studentClubRegistrationRepository::save);
    }

    /**
     * Get all the studentClubRegistrations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StudentClubRegistration> findAll(Pageable pageable) {
        LOG.debug("Request to get all StudentClubRegistrations");
        return studentClubRegistrationRepository.findAll(pageable);
    }

    /**
     * Get all the studentClubRegistrations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StudentClubRegistration> findAllWithEagerRelationships(Pageable pageable) {
        return studentClubRegistrationRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one studentClubRegistration by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StudentClubRegistration> findOne(Long id) {
        LOG.debug("Request to get StudentClubRegistration : {}", id);
        return studentClubRegistrationRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the studentClubRegistration by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StudentClubRegistration : {}", id);
        studentClubRegistrationRepository.deleteById(id);
    }
}
