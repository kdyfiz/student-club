package com.example.schoolclub.web.rest;

import static com.example.schoolclub.domain.ClubAsserts.*;
import static com.example.schoolclub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.schoolclub.IntegrationTest;
import com.example.schoolclub.domain.Club;
import com.example.schoolclub.repository.ClubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClubResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClubResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_MEMBERS = 1;
    private static final Integer UPDATED_MAX_MEMBERS = 2;

    private static final String ENTITY_API_URL = "/api/clubs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClubMockMvc;

    private Club club;

    private Club insertedClub;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Club createEntity() {
        return new Club().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).maxMembers(DEFAULT_MAX_MEMBERS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Club createUpdatedEntity() {
        return new Club().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).maxMembers(UPDATED_MAX_MEMBERS);
    }

    @BeforeEach
    void initTest() {
        club = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClub != null) {
            clubRepository.delete(insertedClub);
            insertedClub = null;
        }
    }

    @Test
    @Transactional
    void createClub() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Club
        var returnedClub = om.readValue(
            restClubMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Club.class
        );

        // Validate the Club in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClubUpdatableFieldsEquals(returnedClub, getPersistedClub(returnedClub));

        insertedClub = returnedClub;
    }

    @Test
    @Transactional
    void createClubWithExistingId() throws Exception {
        // Create the Club with an existing ID
        club.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        club.setName(null);

        // Create the Club, which fails.

        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxMembersIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        club.setMaxMembers(null);

        // Create the Club, which fails.

        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClubs() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        // Get all the clubList
        restClubMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(club.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].maxMembers").value(hasItem(DEFAULT_MAX_MEMBERS)));
    }

    @Test
    @Transactional
    void getClub() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        // Get the club
        restClubMockMvc
            .perform(get(ENTITY_API_URL_ID, club.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(club.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.maxMembers").value(DEFAULT_MAX_MEMBERS));
    }

    @Test
    @Transactional
    void getNonExistingClub() throws Exception {
        // Get the club
        restClubMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClub() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the club
        Club updatedClub = clubRepository.findById(club.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClub are not directly saved in db
        em.detach(updatedClub);
        updatedClub.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).maxMembers(UPDATED_MAX_MEMBERS);

        restClubMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClub.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClubToMatchAllProperties(updatedClub);
    }

    @Test
    @Transactional
    void putNonExistingClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(put(ENTITY_API_URL_ID, club.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(club)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClubWithPatch() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the club using partial update
        Club partialUpdatedClub = new Club();
        partialUpdatedClub.setId(club.getId());

        partialUpdatedClub.name(UPDATED_NAME);

        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClub.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClubUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClub, club), getPersistedClub(club));
    }

    @Test
    @Transactional
    void fullUpdateClubWithPatch() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the club using partial update
        Club partialUpdatedClub = new Club();
        partialUpdatedClub.setId(club.getId());

        partialUpdatedClub.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).maxMembers(UPDATED_MAX_MEMBERS);

        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClub.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClubUpdatableFieldsEquals(partialUpdatedClub, getPersistedClub(partialUpdatedClub));
    }

    @Test
    @Transactional
    void patchNonExistingClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(patch(ENTITY_API_URL_ID, club.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(club)))
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClub() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        club.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(club)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Club in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClub() throws Exception {
        // Initialize the database
        insertedClub = clubRepository.saveAndFlush(club);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the club
        restClubMockMvc
            .perform(delete(ENTITY_API_URL_ID, club.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clubRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Club getPersistedClub(Club club) {
        return clubRepository.findById(club.getId()).orElseThrow();
    }

    protected void assertPersistedClubToMatchAllProperties(Club expectedClub) {
        assertClubAllPropertiesEquals(expectedClub, getPersistedClub(expectedClub));
    }

    protected void assertPersistedClubToMatchUpdatableProperties(Club expectedClub) {
        assertClubAllUpdatablePropertiesEquals(expectedClub, getPersistedClub(expectedClub));
    }
}
