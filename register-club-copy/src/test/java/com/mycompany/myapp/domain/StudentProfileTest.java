package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.StudentClubRegistrationTestSamples.*;
import static com.mycompany.myapp.domain.StudentProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentProfile.class);
        StudentProfile studentProfile1 = getStudentProfileSample1();
        StudentProfile studentProfile2 = new StudentProfile();
        assertThat(studentProfile1).isNotEqualTo(studentProfile2);

        studentProfile2.setId(studentProfile1.getId());
        assertThat(studentProfile1).isEqualTo(studentProfile2);

        studentProfile2 = getStudentProfileSample2();
        assertThat(studentProfile1).isNotEqualTo(studentProfile2);
    }

    @Test
    void studentClubRegistrationTest() {
        StudentProfile studentProfile = getStudentProfileRandomSampleGenerator();
        StudentClubRegistration studentClubRegistrationBack = getStudentClubRegistrationRandomSampleGenerator();

        studentProfile.setStudentClubRegistration(studentClubRegistrationBack);
        assertThat(studentProfile.getStudentClubRegistration()).isEqualTo(studentClubRegistrationBack);

        studentProfile.studentClubRegistration(null);
        assertThat(studentProfile.getStudentClubRegistration()).isNull();
    }
}
