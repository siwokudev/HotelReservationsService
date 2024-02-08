package com.coherent.solutions.test.hotelreservationsservice.service;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;

public interface ReservationService {
    ReservationResponseDTO saveReservation(ReservationRequestDTO reservationRequest);
}
