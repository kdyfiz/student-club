package com.example.schoolclub.service.mapper;

import static com.example.schoolclub.domain.RegistrationAsserts.*;
import static com.example.schoolclub.domain.RegistrationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationMapperTest {

    private RegistrationMapper registrationMapper;

    @BeforeEach
    void setUp() {
        registrationMapper = new RegistrationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRegistrationSample1();
        var actual = registrationMapper.toEntity(registrationMapper.toDto(expected));
        assertRegistrationAllPropertiesEquals(expected, actual);
    }
}
