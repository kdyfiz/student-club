package com.example.schoolclub.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClubTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Club getClubSample1() {
        return new Club().id(1L).name("name1").description("description1").maxMembers(1);
    }

    public static Club getClubSample2() {
        return new Club().id(2L).name("name2").description("description2").maxMembers(2);
    }

    public static Club getClubRandomSampleGenerator() {
        return new Club()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .maxMembers(intCount.incrementAndGet());
    }
}
