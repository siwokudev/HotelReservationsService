package com.coherent.solutions.test.hotelreservationsservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "clientFullName must not be blank")
    @NotNull(message = "clientFullName must not be null")
    private String clientFullName;

    @Min(value = 1, message = "roomNumber must be greater than 0")
    private int roomNumber;

    private LocalDate startDate;

    private LocalDate endDate;
}
