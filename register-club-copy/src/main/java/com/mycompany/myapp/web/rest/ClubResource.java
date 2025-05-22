package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Club;
import com.mycompany.myapp.repository.ClubRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Club}.
 */
@RestController
@RequestMapping("/api/clubs")
@Transactional
public class ClubResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClubResource.class);

    private static final String ENTITY_NAME = "club";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClubRepository clubRepository;

    public ClubResource(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * {@code POST  /clubs} : Create a new club.
     *
     * @param club the club to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new club, or with status {@code 400 (Bad Request)} if the club has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Club> createClub(@Valid @RequestBody Club club) throws URISyntaxException {
        LOG.debug("REST request to save Club : {}", club);
        if (club.getId() != null) {
            throw new BadRequestAlertException("A new club cannot already have an ID", ENTITY_NAME, "idexists");
        }
        club = clubRepository.save(club);
        return ResponseEntity.created(new URI("/api/clubs/" + club.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, club.getId().toString()))
            .body(club);
    }

    /**
     * {@code PUT  /clubs/:id} : Updates an existing club.
     *
     * @param id the id of the club to save.
     * @param club the club to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated club,
     * or with status {@code 400 (Bad Request)} if the club is not valid,
     * or with status {@code 500 (Internal Server Error)} if the club couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Club club)
        throws URISyntaxException {
        LOG.debug("REST request to update Club : {}, {}", id, club);
        if (club.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, club.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clubRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        club = clubRepository.save(club);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, club.getId().toString()))
            .body(club);
    }

    /**
     * {@code PATCH  /clubs/:id} : Partial updates given fields of an existing club, field will ignore if it is null
     *
     * @param id the id of the club to save.
     * @param club the club to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated club,
     * or with status {@code 400 (Bad Request)} if the club is not valid,
     * or with status {@code 404 (Not Found)} if the club is not found,
     * or with status {@code 500 (Internal Server Error)} if the club couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Club> partialUpdateClub(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Club club
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Club partially : {}, {}", id, club);
        if (club.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, club.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clubRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Club> result = clubRepository
            .findById(club.getId())
            .map(existingClub -> {
                if (club.getName() != null) {
                    existingClub.setName(club.getName());
                }
                if (club.getDescription() != null) {
                    existingClub.setDescription(club.getDescription());
                }

                return existingClub;
            })
            .map(clubRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, club.getId().toString())
        );
    }

    /**
     * {@code GET  /clubs} : get all the clubs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clubs in body.
     */
    @GetMapping("")
    public List<Club> getAllClubs() {
        LOG.debug("REST request to get all Clubs");
        return clubRepository.findAll();
    }

    /**
     * {@code GET  /clubs/:id} : get the "id" club.
     *
     * @param id the id of the club to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the club, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Club> getClub(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Club : {}", id);
        Optional<Club> club = clubRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(club);
    }

    /**
     * {@code DELETE  /clubs/:id} : delete the "id" club.
     *
     * @param id the id of the club to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Club : {}", id);
        clubRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
