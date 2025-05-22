package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StudentClubRegistrationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StudentClubRegistration getStudentClubRegistrationSample1() {
        return new StudentClubRegistration().id(1L);
    }

    public static StudentClubRegistration getStudentClubRegistrationSample2() {
        return new StudentClubRegistration().id(2L);
    }

    public static StudentClubRegistration getStudentClubRegistrationRandomSampleGenerator() {
        return new StudentClubRegistration().id(longCount.incrementAndGet());
    }
}
