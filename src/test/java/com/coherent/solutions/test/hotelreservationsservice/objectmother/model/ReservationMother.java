package com.coherent.solutions.test.hotelreservationsservice.objectmother.model;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.utils.DatesMother;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Arrays;

public class ReservationMother {
    private static Faker faker = Faker.instance();

    public static Reservation.ReservationBuilder complete() {

        LocalDate startDate = DatesMother.inTheFuture();
        LocalDate endDate = DatesMother.inTheFutureFromDate(startDate);

        return Reservation.builder()
                .id(faker.number().numberBetween(1,999))
                .roomNumber(faker.number().numberBetween(1,999))
                .clientFullName(faker.name().fullName())
                .reservationDates(
                        Arrays.asList(startDate, endDate));
    }

    public static Reservation.ReservationBuilder inThePast() {
        LocalDate endDate = DatesMother.inThePast();
        LocalDate startDate = DatesMother.inThePastFromDate(endDate);
        return  complete()
                .reservationDates(
                        Arrays.asList(startDate,endDate));
    }

    public static Reservation.ReservationBuilder fromRequest(ReservationRequestDTO request) {
        return Reservation.builder()
                .id(faker.number().numberBetween(1,999))
                .roomNumber(request.getRoomNumber())
                .clientFullName(request.getClientFullName())
                .reservationDates(
                        Arrays.asList(request.getStartDate(), request.getEndDate())
                );
    }
}
