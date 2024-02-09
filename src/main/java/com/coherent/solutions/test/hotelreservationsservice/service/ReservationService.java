package com.coherent.solutions.test.hotelreservationsservice.service;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO saveReservation(ReservationRequestDTO reservationRequest);
    ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequest);
    List<ReservationResponseDTO> getReservations();
    void deleteReservation(int id);
}
