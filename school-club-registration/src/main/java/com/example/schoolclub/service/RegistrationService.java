package com.example.schoolclub.service;

import com.example.schoolclub.domain.Registration;
import com.example.schoolclub.repository.RegistrationRepository;
import com.example.schoolclub.service.dto.RegistrationDTO;
import com.example.schoolclub.service.mapper.RegistrationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.example.schoolclub.domain.Registration}.
 */
@Service
@Transactional
public class RegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);

    private final RegistrationRepository registrationRepository;

    private final RegistrationMapper registrationMapper;

    public RegistrationService(RegistrationRepository registrationRepository, RegistrationMapper registrationMapper) {
        this.registrationRepository = registrationRepository;
        this.registrationMapper = registrationMapper;
    }

    /**
     * Save a registration.
     *
     * @param registrationDTO the entity to save.
     * @return the persisted entity.
     */
    public RegistrationDTO save(RegistrationDTO registrationDTO) {
        LOG.debug("Request to save Registration : {}", registrationDTO);
        Registration registration = registrationMapper.toEntity(registrationDTO);
        registration = registrationRepository.save(registration);
        return registrationMapper.toDto(registration);
    }

    /**
     * Update a registration.
     *
     * @param registrationDTO the entity to save.
     * @return the persisted entity.
     */
    public RegistrationDTO update(RegistrationDTO registrationDTO) {
        LOG.debug("Request to update Registration : {}", registrationDTO);
        Registration registration = registrationMapper.toEntity(registrationDTO);
        registration = registrationRepository.save(registration);
        return registrationMapper.toDto(registration);
    }

    /**
     * Partially update a registration.
     *
     * @param registrationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RegistrationDTO> partialUpdate(RegistrationDTO registrationDTO) {
        LOG.debug("Request to partially update Registration : {}", registrationDTO);

        return registrationRepository
            .findById(registrationDTO.getId())
            .map(existingRegistration -> {
                registrationMapper.partialUpdate(existingRegistration, registrationDTO);

                return existingRegistration;
            })
            .map(registrationRepository::save)
            .map(registrationMapper::toDto);
    }

    /**
     * Get all the registrations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RegistrationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Registrations");
        return registrationRepository.findAll(pageable).map(registrationMapper::toDto);
    }

    /**
     * Get all the registrations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RegistrationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return registrationRepository.findAllWithEagerRelationships(pageable).map(registrationMapper::toDto);
    }

    /**
     * Get one registration by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RegistrationDTO> findOne(Long id) {
        LOG.debug("Request to get Registration : {}", id);
        return registrationRepository.findOneWithEagerRelationships(id).map(registrationMapper::toDto);
    }

    /**
     * Delete the registration by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Registration : {}", id);
        registrationRepository.deleteById(id);
    }
}
