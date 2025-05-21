package com.example.schoolclub.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RegistrationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Registration getRegistrationSample1() {
        return new Registration().id(1L);
    }

    public static Registration getRegistrationSample2() {
        return new Registration().id(2L);
    }

    public static Registration getRegistrationRandomSampleGenerator() {
        return new Registration().id(longCount.incrementAndGet());
    }
}
