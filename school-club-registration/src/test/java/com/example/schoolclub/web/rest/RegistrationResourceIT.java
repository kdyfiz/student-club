package com.example.schoolclub.web.rest;

import static com.example.schoolclub.domain.RegistrationAsserts.*;
import static com.example.schoolclub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.schoolclub.IntegrationTest;
import com.example.schoolclub.domain.Club;
import com.example.schoolclub.domain.Registration;
import com.example.schoolclub.domain.User;
import com.example.schoolclub.domain.enumeration.RegistrationStatus;
import com.example.schoolclub.repository.RegistrationRepository;
import com.example.schoolclub.repository.UserRepository;
import com.example.schoolclub.service.RegistrationService;
import com.example.schoolclub.service.dto.RegistrationDTO;
import com.example.schoolclub.service.mapper.RegistrationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link RegistrationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RegistrationResourceIT {

    private static final Instant DEFAULT_REGISTRATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REGISTRATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final RegistrationStatus DEFAULT_STATUS = RegistrationStatus.PENDING;
    private static final RegistrationStatus UPDATED_STATUS = RegistrationStatus.APPROVED;

    private static final String ENTITY_API_URL = "/api/registrations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepositoryMock;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Mock
    private RegistrationService registrationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRegistrationMockMvc;

    private Registration registration;

    private Registration insertedRegistration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Registration createEntity(EntityManager em) {
        Registration registration = new Registration().registrationDate(DEFAULT_REGISTRATION_DATE).status(DEFAULT_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        registration.setUser(user);
        // Add required entity
        Club club;
        if (TestUtil.findAll(em, Club.class).isEmpty()) {
            club = ClubResourceIT.createEntity();
            em.persist(club);
            em.flush();
        } else {
            club = TestUtil.findAll(em, Club.class).get(0);
        }
        registration.setClub(club);
        return registration;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Registration createUpdatedEntity(EntityManager em) {
        Registration updatedRegistration = new Registration().registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedRegistration.setUser(user);
        // Add required entity
        Club club;
        if (TestUtil.findAll(em, Club.class).isEmpty()) {
            club = ClubResourceIT.createUpdatedEntity();
            em.persist(club);
            em.flush();
        } else {
            club = TestUtil.findAll(em, Club.class).get(0);
        }
        updatedRegistration.setClub(club);
        return updatedRegistration;
    }

    @BeforeEach
    void initTest() {
        registration = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRegistration != null) {
            registrationRepository.delete(insertedRegistration);
            insertedRegistration = null;
        }
    }

    @Test
    @Transactional
    void createRegistration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);
        var returnedRegistrationDTO = om.readValue(
            restRegistrationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RegistrationDTO.class
        );

        // Validate the Registration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRegistration = registrationMapper.toEntity(returnedRegistrationDTO);
        assertRegistrationUpdatableFieldsEquals(returnedRegistration, getPersistedRegistration(returnedRegistration));

        insertedRegistration = returnedRegistration;
    }

    @Test
    @Transactional
    void createRegistrationWithExistingId() throws Exception {
        // Create the Registration with an existing ID
        registration.setId(1L);
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(registrationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRegistrationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        registration.setRegistrationDate(null);

        // Create the Registration, which fails.
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        restRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(registrationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        registration.setStatus(null);

        // Create the Registration, which fails.
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        restRegistrationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(registrationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRegistrations() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        // Get all the registrationList
        restRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(registration.getId().intValue())))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegistrationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(registrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(registrationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegistrationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(registrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(registrationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRegistration() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        // Get the registration
        restRegistrationMockMvc
            .perform(get(ENTITY_API_URL_ID, registration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(registration.getId().intValue()))
            .andExpect(jsonPath("$.registrationDate").value(DEFAULT_REGISTRATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRegistration() throws Exception {
        // Get the registration
        restRegistrationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRegistration() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the registration
        Registration updatedRegistration = registrationRepository.findById(registration.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRegistration are not directly saved in db
        em.detach(updatedRegistration);
        updatedRegistration.registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);
        RegistrationDTO registrationDTO = registrationMapper.toDto(updatedRegistration);

        restRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, registrationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(registrationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRegistrationToMatchAllProperties(updatedRegistration);
    }

    @Test
    @Transactional
    void putNonExistingRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, registrationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(registrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(registrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(registrationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRegistrationWithPatch() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the registration using partial update
        Registration partialUpdatedRegistration = new Registration();
        partialUpdatedRegistration.setId(registration.getId());

        partialUpdatedRegistration.status(UPDATED_STATUS);

        restRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegistration))
            )
            .andExpect(status().isOk());

        // Validate the Registration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegistrationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRegistration, registration),
            getPersistedRegistration(registration)
        );
    }

    @Test
    @Transactional
    void fullUpdateRegistrationWithPatch() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the registration using partial update
        Registration partialUpdatedRegistration = new Registration();
        partialUpdatedRegistration.setId(registration.getId());

        partialUpdatedRegistration.registrationDate(UPDATED_REGISTRATION_DATE).status(UPDATED_STATUS);

        restRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegistration))
            )
            .andExpect(status().isOk());

        // Validate the Registration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegistrationUpdatableFieldsEquals(partialUpdatedRegistration, getPersistedRegistration(partialUpdatedRegistration));
    }

    @Test
    @Transactional
    void patchNonExistingRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, registrationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(registrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(registrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRegistration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        registration.setId(longCount.incrementAndGet());

        // Create the Registration
        RegistrationDTO registrationDTO = registrationMapper.toDto(registration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegistrationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(registrationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Registration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRegistration() throws Exception {
        // Initialize the database
        insertedRegistration = registrationRepository.saveAndFlush(registration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the registration
        restRegistrationMockMvc
            .perform(delete(ENTITY_API_URL_ID, registration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return registrationRepository.count();
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

    protected Registration getPersistedRegistration(Registration registration) {
        return registrationRepository.findById(registration.getId()).orElseThrow();
    }

    protected void assertPersistedRegistrationToMatchAllProperties(Registration expectedRegistration) {
        assertRegistrationAllPropertiesEquals(expectedRegistration, getPersistedRegistration(expectedRegistration));
    }

    protected void assertPersistedRegistrationToMatchUpdatableProperties(Registration expectedRegistration) {
        assertRegistrationAllUpdatablePropertiesEquals(expectedRegistration, getPersistedRegistration(expectedRegistration));
    }
}
