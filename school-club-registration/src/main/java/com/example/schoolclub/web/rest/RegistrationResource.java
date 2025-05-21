package com.example.schoolclub.web.rest;

import com.example.schoolclub.repository.RegistrationRepository;
import com.example.schoolclub.service.RegistrationService;
import com.example.schoolclub.service.dto.RegistrationDTO;
import com.example.schoolclub.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.schoolclub.domain.Registration}.
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationResource.class);

    private static final String ENTITY_NAME = "registration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RegistrationService registrationService;

    private final RegistrationRepository registrationRepository;

    public RegistrationResource(RegistrationService registrationService, RegistrationRepository registrationRepository) {
        this.registrationService = registrationService;
        this.registrationRepository = registrationRepository;
    }

    /**
     * {@code POST  /registrations} : Create a new registration.
     *
     * @param registrationDTO the registrationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new registrationDTO, or with status {@code 400 (Bad Request)} if the registration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RegistrationDTO> createRegistration(@Valid @RequestBody RegistrationDTO registrationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Registration : {}", registrationDTO);
        if (registrationDTO.getId() != null) {
            throw new BadRequestAlertException("A new registration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        registrationDTO = registrationService.save(registrationDTO);
        return ResponseEntity.created(new URI("/api/registrations/" + registrationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, registrationDTO.getId().toString()))
            .body(registrationDTO);
    }

    /**
     * {@code PUT  /registrations/:id} : Updates an existing registration.
     *
     * @param id the id of the registrationDTO to save.
     * @param registrationDTO the registrationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated registrationDTO,
     * or with status {@code 400 (Bad Request)} if the registrationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the registrationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationDTO> updateRegistration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RegistrationDTO registrationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Registration : {}, {}", id, registrationDTO);
        if (registrationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, registrationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!registrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        registrationDTO = registrationService.update(registrationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, registrationDTO.getId().toString()))
            .body(registrationDTO);
    }

    /**
     * {@code PATCH  /registrations/:id} : Partial updates given fields of an existing registration, field will ignore if it is null
     *
     * @param id the id of the registrationDTO to save.
     * @param registrationDTO the registrationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated registrationDTO,
     * or with status {@code 400 (Bad Request)} if the registrationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the registrationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the registrationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RegistrationDTO> partialUpdateRegistration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RegistrationDTO registrationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Registration partially : {}, {}", id, registrationDTO);
        if (registrationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, registrationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!registrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RegistrationDTO> result = registrationService.partialUpdate(registrationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, registrationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /registrations} : get all the registrations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of registrations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Registrations");
        Page<RegistrationDTO> page;
        if (eagerload) {
            page = registrationService.findAllWithEagerRelationships(pageable);
        } else {
            page = registrationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /registrations/:id} : get the "id" registration.
     *
     * @param id the id of the registrationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the registrationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Registration : {}", id);
        Optional<RegistrationDTO> registrationDTO = registrationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(registrationDTO);
    }

    /**
     * {@code DELETE  /registrations/:id} : delete the "id" registration.
     *
     * @param id the id of the registrationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Registration : {}", id);
        registrationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
