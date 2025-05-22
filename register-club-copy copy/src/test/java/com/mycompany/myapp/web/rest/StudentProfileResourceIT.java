package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StudentProfileAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.repository.StudentProfileRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.StudentProfileService;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link StudentProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentProfileResourceIT {

    private static final String DEFAULT_STUDENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GRADE = "AAAAAAAAAA";
    private static final String UPDATED_GRADE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/student-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepositoryMock;

    @Mock
    private StudentProfileService studentProfileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentProfileMockMvc;

    private StudentProfile studentProfile;

    private StudentProfile insertedStudentProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentProfile createEntity() {
        return new StudentProfile().studentId(DEFAULT_STUDENT_ID).fullName(DEFAULT_FULL_NAME).grade(DEFAULT_GRADE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentProfile createUpdatedEntity() {
        return new StudentProfile().studentId(UPDATED_STUDENT_ID).fullName(UPDATED_FULL_NAME).grade(UPDATED_GRADE);
    }

    @BeforeEach
    void initTest() {
        studentProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentProfile != null) {
            studentProfileRepository.delete(insertedStudentProfile);
            insertedStudentProfile = null;
        }
    }

    @Test
    @Transactional
    void createStudentProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentProfile
        var returnedStudentProfile = om.readValue(
            restStudentProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentProfile.class
        );

        // Validate the StudentProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStudentProfileUpdatableFieldsEquals(returnedStudentProfile, getPersistedStudentProfile(returnedStudentProfile));

        insertedStudentProfile = returnedStudentProfile;
    }

    @Test
    @Transactional
    void createStudentProfileWithExistingId() throws Exception {
        // Create the StudentProfile with an existing ID
        studentProfile.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isBadRequest());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStudentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentProfile.setStudentId(null);

        // Create the StudentProfile, which fails.

        restStudentProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentProfile.setFullName(null);

        // Create the StudentProfile, which fails.

        restStudentProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGradeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentProfile.setGrade(null);

        // Create the StudentProfile, which fails.

        restStudentProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentProfiles() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        // Get all the studentProfileList
        restStudentProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].studentId").value(hasItem(DEFAULT_STUDENT_ID)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentProfile() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        // Get the studentProfile
        restStudentProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, studentProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentProfile.getId().intValue()))
            .andExpect(jsonPath("$.studentId").value(DEFAULT_STUDENT_ID))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE));
    }

    @Test
    @Transactional
    void getNonExistingStudentProfile() throws Exception {
        // Get the studentProfile
        restStudentProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentProfile() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile
        StudentProfile updatedStudentProfile = studentProfileRepository.findById(studentProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudentProfile are not directly saved in db
        em.detach(updatedStudentProfile);
        updatedStudentProfile.studentId(UPDATED_STUDENT_ID).fullName(UPDATED_FULL_NAME).grade(UPDATED_GRADE);

        restStudentProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStudentProfile.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStudentProfile))
            )
            .andExpect(status().isOk());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentProfileToMatchAllProperties(updatedStudentProfile);
    }

    @Test
    @Transactional
    void putNonExistingStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentProfile.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentProfileWithPatch() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile using partial update
        StudentProfile partialUpdatedStudentProfile = new StudentProfile();
        partialUpdatedStudentProfile.setId(studentProfile.getId());

        partialUpdatedStudentProfile.studentId(UPDATED_STUDENT_ID).fullName(UPDATED_FULL_NAME);

        restStudentProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentProfile))
            )
            .andExpect(status().isOk());

        // Validate the StudentProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentProfile, studentProfile),
            getPersistedStudentProfile(studentProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentProfileWithPatch() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile using partial update
        StudentProfile partialUpdatedStudentProfile = new StudentProfile();
        partialUpdatedStudentProfile.setId(studentProfile.getId());

        partialUpdatedStudentProfile.studentId(UPDATED_STUDENT_ID).fullName(UPDATED_FULL_NAME).grade(UPDATED_GRADE);

        restStudentProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentProfile))
            )
            .andExpect(status().isOk());

        // Validate the StudentProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentProfileUpdatableFieldsEquals(partialUpdatedStudentProfile, getPersistedStudentProfile(partialUpdatedStudentProfile));
    }

    @Test
    @Transactional
    void patchNonExistingStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentProfile)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentProfile() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.saveAndFlush(studentProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentProfile
        restStudentProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentProfileRepository.count();
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

    protected StudentProfile getPersistedStudentProfile(StudentProfile studentProfile) {
        return studentProfileRepository.findById(studentProfile.getId()).orElseThrow();
    }

    protected void assertPersistedStudentProfileToMatchAllProperties(StudentProfile expectedStudentProfile) {
        assertStudentProfileAllPropertiesEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
    }

    protected void assertPersistedStudentProfileToMatchUpdatableProperties(StudentProfile expectedStudentProfile) {
        assertStudentProfileAllUpdatablePropertiesEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
    }
}
