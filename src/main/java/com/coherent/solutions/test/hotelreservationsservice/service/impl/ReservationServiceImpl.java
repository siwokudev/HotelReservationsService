package com.coherent.solutions.test.hotelreservationsservice.service.impl;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import com.coherent.solutions.test.hotelreservationsservice.repository.ReservationRepository;
import com.coherent.solutions.test.hotelreservationsservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    @Override
    public ReservationResponseDTO saveReservation(ReservationRequestDTO reservationRequest) {
        Reservation reservation = mapReservationRequestDTOToModel(reservationRequest);
        //TODO validate there are no reservations for the same roomNumber during the reservation dates
        //if so, return error
        reservation = reservationRepository.save(reservation);
        return mapModelToReservationResponseDTO(reservation);
    }

    @Override
    public List<ReservationResponseDTO> getReservations() {
        List<ReservationResponseDTO> reservations = reservationRepository.findAll().stream()
                .map(reservation -> mapModelToReservationResponseDTO(reservation))
                .collect(Collectors.toList());
        return reservations;
    }

    private Reservation mapReservationRequestDTOToModel(ReservationRequestDTO reservationRequest) {
        return Reservation.builder()
                .clientFullName(reservationRequest.getClientFullName())
                .roomNumber(reservationRequest.getRoomNumber())
                .reservationDates(Arrays.asList( reservationRequest.getStartDate(),
                                reservationRequest.getEndDate()))
                .build();
    }

    private ReservationResponseDTO mapModelToReservationResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .clientFullName(reservation.getClientFullName())
                .roomNumber(reservation.getRoomNumber())
                .reservationDates(reservation.getReservationDates())
                .build();
    }
}
