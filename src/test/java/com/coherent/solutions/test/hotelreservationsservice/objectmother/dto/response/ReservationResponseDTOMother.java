package com.coherent.solutions.test.hotelreservationsservice.objectmother.dto.response;

import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.utils.DatesMother;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Arrays;

public class ReservationResponseDTOMother {

    private static Faker faker = Faker.instance();
    public static ReservationResponseDTO.ReservationResponseDTOBuilder complete() {

        LocalDate startDate = DatesMother.inTheFuture();
        LocalDate endDate = DatesMother.inTheFutureFromDate(startDate);

        return ReservationResponseDTO.builder()
                .clientFullName(faker.name().fullName())
                .roomNumber(faker.number().numberBetween(1,999))
                .reservationDates(
                        Arrays.asList(startDate, endDate));
    }

}
