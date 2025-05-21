package com.example.schoolclub.domain;

import static com.example.schoolclub.domain.ClubTestSamples.*;
import static com.example.schoolclub.domain.RegistrationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.schoolclub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClubTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Club.class);
        Club club1 = getClubSample1();
        Club club2 = new Club();
        assertThat(club1).isNotEqualTo(club2);

        club2.setId(club1.getId());
        assertThat(club1).isEqualTo(club2);

        club2 = getClubSample2();
        assertThat(club1).isNotEqualTo(club2);
    }

    @Test
    void registrationsTest() {
        Club club = getClubRandomSampleGenerator();
        Registration registrationBack = getRegistrationRandomSampleGenerator();

        club.addRegistrations(registrationBack);
        assertThat(club.getRegistrations()).containsOnly(registrationBack);
        assertThat(registrationBack.getClub()).isEqualTo(club);

        club.removeRegistrations(registrationBack);
        assertThat(club.getRegistrations()).doesNotContain(registrationBack);
        assertThat(registrationBack.getClub()).isNull();

        club.registrations(new HashSet<>(Set.of(registrationBack)));
        assertThat(club.getRegistrations()).containsOnly(registrationBack);
        assertThat(registrationBack.getClub()).isEqualTo(club);

        club.setRegistrations(new HashSet<>());
        assertThat(club.getRegistrations()).doesNotContain(registrationBack);
        assertThat(registrationBack.getClub()).isNull();
    }
}
