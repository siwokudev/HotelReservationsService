package com.coherent.solutions.test.hotelreservationsservice.service.impl;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.BadRequestException;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.ResourceNotFoundException;
import com.coherent.solutions.test.hotelreservationsservice.mappers.ReservationMapper;
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
        Reservation reservation = ReservationMapper.INSTANCE.reservationRequestDTOToReservation(reservationRequest);
        //TODO validate there are no reservations for the same roomNumber during the reservation dates
        //if so, return error
        reservation = reservationRepository.save(reservation);
        return ReservationMapper.INSTANCE.reservationToReservationResponseDTO(reservation);
    }

    @Override
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequest) {
        Reservation reservation = ReservationMapper.INSTANCE.reservationRequestDTOToReservation(reservationRequest);
        reservation.setId(id);

        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("reservation not found with id: %d", id));
        }
        //TODO validate there are no reservations for the same roomNumber during the reservation dates
        //if so, return error

        reservation = reservationRepository.save(reservation);
        return ReservationMapper.INSTANCE.reservationToReservationResponseDTO(reservation);
    }

    @Override
    public List<ReservationResponseDTO> getReservations() {
        List<ReservationResponseDTO> reservations = reservationRepository.findAll().stream()
                .map(ReservationMapper.INSTANCE::reservationToReservationResponseDTO)
                .collect(Collectors.toList());
        return reservations;
    }
}
