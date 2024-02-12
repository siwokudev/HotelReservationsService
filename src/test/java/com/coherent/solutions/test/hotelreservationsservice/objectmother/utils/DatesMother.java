package com.coherent.solutions.test.hotelreservationsservice.objectmother.utils;

import com.github.javafaker.Faker;

import java.time.LocalDate;

public class DatesMother {

    private static Faker faker = Faker.instance();

    public static LocalDate inTheFuture() {
        int days = faker.number().numberBetween(1, 200);
        return LocalDate.now().plusDays(days);
    }

    public static LocalDate inTheFutureFromDate(LocalDate from) {
        int days = faker.number().numberBetween(1, 200);
        return from.plusDays(days);
    }

    public static LocalDate inThePast() {
        int days = faker.number().numberBetween(1, 200);
        return LocalDate.now().minusDays(days);
    }

    public static LocalDate inThePastFromDate(LocalDate from) {
        int days = faker.number().numberBetween(1, 200);
        return from.minusDays(days);
    }
}
