package com.coherent.solutions.test.hotelreservationsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseDTO {
    private int id;
    private String clientFullName;
    private int roomNumber;
    private List<LocalDate> reservationDates;
}
