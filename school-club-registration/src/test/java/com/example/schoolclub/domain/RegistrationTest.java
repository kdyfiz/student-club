package com.example.schoolclub.domain;

import static com.example.schoolclub.domain.ClubTestSamples.*;
import static com.example.schoolclub.domain.RegistrationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.schoolclub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegistrationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Registration.class);
        Registration registration1 = getRegistrationSample1();
        Registration registration2 = new Registration();
        assertThat(registration1).isNotEqualTo(registration2);

        registration2.setId(registration1.getId());
        assertThat(registration1).isEqualTo(registration2);

        registration2 = getRegistrationSample2();
        assertThat(registration1).isNotEqualTo(registration2);
    }

    @Test
    void clubTest() {
        Registration registration = getRegistrationRandomSampleGenerator();
        Club clubBack = getClubRandomSampleGenerator();

        registration.setClub(clubBack);
        assertThat(registration.getClub()).isEqualTo(clubBack);

        registration.club(null);
        assertThat(registration.getClub()).isNull();
    }
}
