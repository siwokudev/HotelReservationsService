package com.coherent.solutions.test.hotelreservationsservice.objectmother.dto.request;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.utils.DatesMother;
import com.github.javafaker.Faker;

import java.time.LocalDate;

public class ReservationRequestDTOMother {
    private static Faker faker = Faker.instance();
    public static ReservationRequestDTO.ReservationRequestDTOBuilder complete() {
        LocalDate startDate = DatesMother.inTheFuture();
        return ReservationRequestDTO.builder()
                .clientFullName(faker.name().fullName())
                .roomNumber(faker.number().numberBetween(1,999))
                .startDate(startDate)
                .endDate(DatesMother.inTheFutureFromDate(startDate));
    }
}
