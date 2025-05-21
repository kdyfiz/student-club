package com.example.schoolclub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.schoolclub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegistrationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegistrationDTO.class);
        RegistrationDTO registrationDTO1 = new RegistrationDTO();
        registrationDTO1.setId(1L);
        RegistrationDTO registrationDTO2 = new RegistrationDTO();
        assertThat(registrationDTO1).isNotEqualTo(registrationDTO2);
        registrationDTO2.setId(registrationDTO1.getId());
        assertThat(registrationDTO1).isEqualTo(registrationDTO2);
        registrationDTO2.setId(2L);
        assertThat(registrationDTO1).isNotEqualTo(registrationDTO2);
        registrationDTO1.setId(null);
        assertThat(registrationDTO1).isNotEqualTo(registrationDTO2);
    }
}
