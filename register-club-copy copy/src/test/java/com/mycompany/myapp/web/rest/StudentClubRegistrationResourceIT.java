package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StudentClubRegistrationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.StudentClubRegistration;
import com.mycompany.myapp.domain.enumeration.RegistrationStatus;
import com.mycompany.myapp.repository.StudentClubRegistrationRepository;
import com.mycompany.myapp.service.StudentClubRegistrationService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StudentClubRegistrationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentClubRegistrationResourceIT {

    private static final Instant DEFAULT_REGISTRATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REGISTRATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final RegistrationStatus DEFAULT_STATUS = RegistrationStatus.PENDING;
    private static final RegistrationStatus UPDATED_STATUS = RegistrationStatus.APPROVED;

    private static final String ENTITY_API_URL = "/api/student-club-registrations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentClubRegistrationRepository studentClubRegistrationRepository;

    @Mock
    private StudentClubRegistrationRepository studentClubRegistrationRepositoryMock;

    @Mock
    private StudentClubRegistrationService studentClubRegistrationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentClubRegistrationMockMvc;

    private StudentClubRegistration studentClubRegistration;

    private StudentClubRegistration insertedStudentClubRegistration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentClubRegistration createEntity() {
        return new StudentClubRegistration().registrationDate(DEFAULT_REGISTRATION_DATE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentClubRegistration createUpdatedEntity() {
        return new StudentClubRegistration().registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        studentClubRegistration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentClubRegistration != null) {
            studentClubRegistrationRepository.delete(insertedStudentClubRegistration);
            insertedStudentClubRegistration = null;
        }
    }

    @Test
    @Transactional
    void createStudentClubRegistration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentClubRegistration
        var returnedStudentClubRegistration = om.readValue(
            restStudentClubRegistrationMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentClubRegistration))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentClubRegistration.class
        );

        // Validate the StudentClubRegistration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStudentClubRegistrationUpdatableFieldsEquals(
            returnedStudentClubRegistration,
            getPersistedStudentClubRegistration(returnedStudentClubRegistration)
        );

        insertedStudentClubRegistration = returnedStudentClubRegistration;
    }

    @Test
    @Transactional
    void createStudentClubRegistrationWithExistingId() throws Exception {
        // Create the StudentClubRegistration with an existing ID
        studentClubRegistration.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentClubRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentClubRegistration)))
            .andExpect(status().isBadRequest());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRegistrationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentClubRegistration.setRegistrationDate(null);

        // Create the StudentClubRegistration, which fails.

        restStudentClubRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentClubRegistration)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentClubRegistration.setStatus(null);

        // Create the StudentClubRegistration, which fails.

        restStudentClubRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentClubRegistration)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentClubRegistrations() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        // Get all the studentClubRegistrationList
        restStudentClubRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentClubRegistration.getId().intValue())))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentClubRegistrationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentClubRegistrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentClubRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentClubRegistrationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentClubRegistrationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentClubRegistrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentClubRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentClubRegistrationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentClubRegistration() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        // Get the studentClubRegistration
        restStudentClubRegistrationMockMvc
            .perform(get(ENTITY_API_URL_ID, studentClubRegistration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentClubRegistration.getId().intValue()))
            .andExpect(jsonPath("$.registrationDate").value(DEFAULT_REGISTRATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStudentClubRegistration() throws Exception {
        // Get the studentClubRegistration
        restStudentClubRegistrationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentClubRegistration() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentClubRegistration
        StudentClubRegistration updatedStudentClubRegistration = studentClubRegistrationRepository
            .findById(studentClubRegistration.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedStudentClubRegistration are not directly saved in db
        em.detach(updatedStudentClubRegistration);
        updatedStudentClubRegistration.registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);

        restStudentClubRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStudentClubRegistration.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStudentClubRegistration))
            )
            .andExpect(status().isOk());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentClubRegistrationToMatchAllProperties(updatedStudentClubRegistration);
    }

    @Test
    @Transactional
    void putNonExistingStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentClubRegistration.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentClubRegistration))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentClubRegistration))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentClubRegistration)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentClubRegistrationWithPatch() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentClubRegistration using partial update
        StudentClubRegistration partialUpdatedStudentClubRegistration = new StudentClubRegistration();
        partialUpdatedStudentClubRegistration.setId(studentClubRegistration.getId());

        partialUpdatedStudentClubRegistration.registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);

        restStudentClubRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentClubRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentClubRegistration))
            )
            .andExpect(status().isOk());

        // Validate the StudentClubRegistration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentClubRegistrationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentClubRegistration, studentClubRegistration),
            getPersistedStudentClubRegistration(studentClubRegistration)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentClubRegistrationWithPatch() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentClubRegistration using partial update
        StudentClubRegistration partialUpdatedStudentClubRegistration = new StudentClubRegistration();
        partialUpdatedStudentClubRegistration.setId(studentClubRegistration.getId());

        partialUpdatedStudentClubRegistration.registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);

        restStudentClubRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentClubRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentClubRegistration))
            )
            .andExpect(status().isOk());

        // Validate the StudentClubRegistration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentClubRegistrationUpdatableFieldsEquals(
            partialUpdatedStudentClubRegistration,
            getPersistedStudentClubRegistration(partialUpdatedStudentClubRegistration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentClubRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentClubRegistration))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentClubRegistration))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentClubRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentClubRegistration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentClubRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentClubRegistration))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentClubRegistration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentClubRegistration() throws Exception {
        // Initialize the database
        insertedStudentClubRegistration = studentClubRegistrationRepository.saveAndFlush(studentClubRegistration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentClubRegistration
        restStudentClubRegistrationMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentClubRegistration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentClubRegistrationRepository.count();
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

    protected StudentClubRegistration getPersistedStudentClubRegistration(StudentClubRegistration studentClubRegistration) {
        return studentClubRegistrationRepository.findById(studentClubRegistration.getId()).orElseThrow();
    }

    protected void assertPersistedStudentClubRegistrationToMatchAllProperties(StudentClubRegistration expectedStudentClubRegistration) {
        assertStudentClubRegistrationAllPropertiesEquals(
            expectedStudentClubRegistration,
            getPersistedStudentClubRegistration(expectedStudentClubRegistration)
        );
    }

    protected void assertPersistedStudentClubRegistrationToMatchUpdatableProperties(
        StudentClubRegistration expectedStudentClubRegistration
    ) {
        assertStudentClubRegistrationAllUpdatablePropertiesEquals(
            expectedStudentClubRegistration,
            getPersistedStudentClubRegistration(expectedStudentClubRegistration)
        );
    }
}
