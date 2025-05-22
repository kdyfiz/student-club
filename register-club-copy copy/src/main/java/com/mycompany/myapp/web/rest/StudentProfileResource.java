package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.repository.StudentProfileRepository;
import com.mycompany.myapp.service.StudentProfileService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.StudentProfile}.
 */
@RestController
@RequestMapping("/api/student-profiles")
public class StudentProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentProfileResource.class);

    private static final String ENTITY_NAME = "studentProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudentProfileService studentProfileService;

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileResource(StudentProfileService studentProfileService, StudentProfileRepository studentProfileRepository) {
        this.studentProfileService = studentProfileService;
        this.studentProfileRepository = studentProfileRepository;
    }

    /**
     * {@code POST  /student-profiles} : Create a new studentProfile.
     *
     * @param studentProfile the studentProfile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentProfile, or with status {@code 400 (Bad Request)} if the studentProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentProfile> createStudentProfile(@Valid @RequestBody StudentProfile studentProfile)
        throws URISyntaxException {
        LOG.debug("REST request to save StudentProfile : {}", studentProfile);
        if (studentProfile.getId() != null) {
            throw new BadRequestAlertException("A new studentProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentProfile = studentProfileService.save(studentProfile);
        return ResponseEntity.created(new URI("/api/student-profiles/" + studentProfile.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentProfile.getId().toString()))
            .body(studentProfile);
    }

    /**
     * {@code PUT  /student-profiles/:id} : Updates an existing studentProfile.
     *
     * @param id the id of the studentProfile to save.
     * @param studentProfile the studentProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentProfile,
     * or with status {@code 400 (Bad Request)} if the studentProfile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentProfile> updateStudentProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentProfile studentProfile
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentProfile : {}, {}", id, studentProfile);
        if (studentProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentProfile = studentProfileService.update(studentProfile);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentProfile.getId().toString()))
            .body(studentProfile);
    }

    /**
     * {@code PATCH  /student-profiles/:id} : Partial updates given fields of an existing studentProfile, field will ignore if it is null
     *
     * @param id the id of the studentProfile to save.
     * @param studentProfile the studentProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentProfile,
     * or with status {@code 400 (Bad Request)} if the studentProfile is not valid,
     * or with status {@code 404 (Not Found)} if the studentProfile is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentProfile> partialUpdateStudentProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentProfile studentProfile
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StudentProfile partially : {}, {}", id, studentProfile);
        if (studentProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentProfile> result = studentProfileService.partialUpdate(studentProfile);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentProfile.getId().toString())
        );
    }

    /**
     * {@code GET  /student-profiles} : get all the studentProfiles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of studentProfiles in body.
     */
    @GetMapping("")
    public List<StudentProfile> getAllStudentProfiles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StudentProfiles");
        return studentProfileService.findAll();
    }

    /**
     * {@code GET  /student-profiles/:id} : get the "id" studentProfile.
     *
     * @param id the id of the studentProfile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentProfile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentProfile> getStudentProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentProfile : {}", id);
        Optional<StudentProfile> studentProfile = studentProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentProfile);
    }

    /**
     * {@code DELETE  /student-profiles/:id} : delete the "id" studentProfile.
     *
     * @param id the id of the studentProfile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentProfile : {}", id);
        studentProfileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
