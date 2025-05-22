package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClubTestSamples.*;
import static com.mycompany.myapp.domain.StudentClubRegistrationTestSamples.*;
import static com.mycompany.myapp.domain.StudentProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudentClubRegistrationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentClubRegistration.class);
        StudentClubRegistration studentClubRegistration1 = getStudentClubRegistrationSample1();
        StudentClubRegistration studentClubRegistration2 = new StudentClubRegistration();
        assertThat(studentClubRegistration1).isNotEqualTo(studentClubRegistration2);

        studentClubRegistration2.setId(studentClubRegistration1.getId());
        assertThat(studentClubRegistration1).isEqualTo(studentClubRegistration2);

        studentClubRegistration2 = getStudentClubRegistrationSample2();
        assertThat(studentClubRegistration1).isNotEqualTo(studentClubRegistration2);
    }

    @Test
    void studentProfileTest() {
        StudentClubRegistration studentClubRegistration = getStudentClubRegistrationRandomSampleGenerator();
        StudentProfile studentProfileBack = getStudentProfileRandomSampleGenerator();

        studentClubRegistration.addStudentProfile(studentProfileBack);
        assertThat(studentClubRegistration.getStudentProfiles()).containsOnly(studentProfileBack);
        assertThat(studentProfileBack.getStudentClubRegistration()).isEqualTo(studentClubRegistration);

        studentClubRegistration.removeStudentProfile(studentProfileBack);
        assertThat(studentClubRegistration.getStudentProfiles()).doesNotContain(studentProfileBack);
        assertThat(studentProfileBack.getStudentClubRegistration()).isNull();

        studentClubRegistration.studentProfiles(new HashSet<>(Set.of(studentProfileBack)));
        assertThat(studentClubRegistration.getStudentProfiles()).containsOnly(studentProfileBack);
        assertThat(studentProfileBack.getStudentClubRegistration()).isEqualTo(studentClubRegistration);

        studentClubRegistration.setStudentProfiles(new HashSet<>());
        assertThat(studentClubRegistration.getStudentProfiles()).doesNotContain(studentProfileBack);
        assertThat(studentProfileBack.getStudentClubRegistration()).isNull();
    }

    @Test
    void clubTest() {
        StudentClubRegistration studentClubRegistration = getStudentClubRegistrationRandomSampleGenerator();
        Club clubBack = getClubRandomSampleGenerator();

        studentClubRegistration.setClub(clubBack);
        assertThat(studentClubRegistration.getClub()).isEqualTo(clubBack);

        studentClubRegistration.club(null);
        assertThat(studentClubRegistration.getClub()).isNull();
    }
}
