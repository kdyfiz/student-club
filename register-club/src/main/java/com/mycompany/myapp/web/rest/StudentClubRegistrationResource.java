package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.StudentClubRegistration;
import com.mycompany.myapp.repository.StudentClubRegistrationRepository;
import com.mycompany.myapp.service.StudentClubRegistrationService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.StudentClubRegistration}.
 */
@RestController
@RequestMapping("/api/student-club-registrations")
public class StudentClubRegistrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentClubRegistrationResource.class);

    private static final String ENTITY_NAME = "studentClubRegistration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudentClubRegistrationService studentClubRegistrationService;

    private final StudentClubRegistrationRepository studentClubRegistrationRepository;

    public StudentClubRegistrationResource(
        StudentClubRegistrationService studentClubRegistrationService,
        StudentClubRegistrationRepository studentClubRegistrationRepository
    ) {
        this.studentClubRegistrationService = studentClubRegistrationService;
        this.studentClubRegistrationRepository = studentClubRegistrationRepository;
    }

    /**
     * {@code POST  /student-club-registrations} : Create a new studentClubRegistration.
     *
     * @param studentClubRegistration the studentClubRegistration to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentClubRegistration, or with status {@code 400 (Bad Request)} if the studentClubRegistration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentClubRegistration> createStudentClubRegistration(
        @Valid @RequestBody StudentClubRegistration studentClubRegistration
    ) throws URISyntaxException {
        LOG.debug("REST request to save StudentClubRegistration : {}", studentClubRegistration);
        if (studentClubRegistration.getId() != null) {
            throw new BadRequestAlertException("A new studentClubRegistration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentClubRegistration = studentClubRegistrationService.save(studentClubRegistration);
        return ResponseEntity.created(new URI("/api/student-club-registrations/" + studentClubRegistration.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentClubRegistration.getId().toString()))
            .body(studentClubRegistration);
    }

    /**
     * {@code PUT  /student-club-registrations/:id} : Updates an existing studentClubRegistration.
     *
     * @param id the id of the studentClubRegistration to save.
     * @param studentClubRegistration the studentClubRegistration to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentClubRegistration,
     * or with status {@code 400 (Bad Request)} if the studentClubRegistration is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentClubRegistration couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentClubRegistration> updateStudentClubRegistration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentClubRegistration studentClubRegistration
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentClubRegistration : {}, {}", id, studentClubRegistration);
        if (studentClubRegistration.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentClubRegistration.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentClubRegistrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentClubRegistration = studentClubRegistrationService.update(studentClubRegistration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentClubRegistration.getId().toString()))
            .body(studentClubRegistration);
    }

    /**
     * {@code PATCH  /student-club-registrations/:id} : Partial updates given fields of an existing studentClubRegistration, field will ignore if it is null
     *
     * @param id the id of the studentClubRegistration to save.
     * @param studentClubRegistration the studentClubRegistration to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentClubRegistration,
     * or with status {@code 400 (Bad Request)} if the studentClubRegistration is not valid,
     * or with status {@code 404 (Not Found)} if the studentClubRegistration is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentClubRegistration couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentClubRegistration> partialUpdateStudentClubRegistration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentClubRegistration studentClubRegistration
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StudentClubRegistration partially : {}, {}", id, studentClubRegistration);
        if (studentClubRegistration.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentClubRegistration.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentClubRegistrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentClubRegistration> result = studentClubRegistrationService.partialUpdate(studentClubRegistration);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentClubRegistration.getId().toString())
        );
    }

    /**
     * {@code GET  /student-club-registrations} : get all the studentClubRegistrations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of studentClubRegistrations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudentClubRegistration>> getAllStudentClubRegistrations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of StudentClubRegistrations");
        Page<StudentClubRegistration> page;
        if (eagerload) {
            page = studentClubRegistrationService.findAllWithEagerRelationships(pageable);
        } else {
            page = studentClubRegistrationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /student-club-registrations/:id} : get the "id" studentClubRegistration.
     *
     * @param id the id of the studentClubRegistration to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentClubRegistration, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentClubRegistration> getStudentClubRegistration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentClubRegistration : {}", id);
        Optional<StudentClubRegistration> studentClubRegistration = studentClubRegistrationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentClubRegistration);
    }

    /**
     * {@code DELETE  /student-club-registrations/:id} : delete the "id" studentClubRegistration.
     *
     * @param id the id of the studentClubRegistration to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentClubRegistration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentClubRegistration : {}", id);
        studentClubRegistrationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
