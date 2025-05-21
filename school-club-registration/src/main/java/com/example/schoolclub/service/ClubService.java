package com.example.schoolclub.service;

import com.example.schoolclub.domain.Club;
import com.example.schoolclub.repository.ClubRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.example.schoolclub.domain.Club}.
 */
@Service
@Transactional
public class ClubService {

    private static final Logger LOG = LoggerFactory.getLogger(ClubService.class);

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * Save a club.
     *
     * @param club the entity to save.
     * @return the persisted entity.
     */
    public Club save(Club club) {
        LOG.debug("Request to save Club : {}", club);
        return clubRepository.save(club);
    }

    /**
     * Update a club.
     *
     * @param club the entity to save.
     * @return the persisted entity.
     */
    public Club update(Club club) {
        LOG.debug("Request to update Club : {}", club);
        return clubRepository.save(club);
    }

    /**
     * Partially update a club.
     *
     * @param club the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Club> partialUpdate(Club club) {
        LOG.debug("Request to partially update Club : {}", club);

        return clubRepository
            .findById(club.getId())
            .map(existingClub -> {
                if (club.getName() != null) {
                    existingClub.setName(club.getName());
                }
                if (club.getDescription() != null) {
                    existingClub.setDescription(club.getDescription());
                }
                if (club.getMaxMembers() != null) {
                    existingClub.setMaxMembers(club.getMaxMembers());
                }

                return existingClub;
            })
            .map(clubRepository::save);
    }

    /**
     * Get all the clubs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Club> findAll(Pageable pageable) {
        LOG.debug("Request to get all Clubs");
        return clubRepository.findAll(pageable);
    }

    /**
     * Get one club by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Club> findOne(Long id) {
        LOG.debug("Request to get Club : {}", id);
        return clubRepository.findById(id);
    }

    /**
     * Delete the club by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Club : {}", id);
        clubRepository.deleteById(id);
    }
}
