package com.coherent.solutions.test.hotelreservationsservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDTO {
    private String clientFullName;
    private int roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;
}
