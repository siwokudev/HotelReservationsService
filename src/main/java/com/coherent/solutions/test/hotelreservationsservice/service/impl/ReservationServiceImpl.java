package com.coherent.solutions.test.hotelreservationsservice.service.impl;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.BadRequestException;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.ResourceNotFoundException;
import com.coherent.solutions.test.hotelreservationsservice.mappers.ReservationMapper;
import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import com.coherent.solutions.test.hotelreservationsservice.repository.ReservationRepository;
import com.coherent.solutions.test.hotelreservationsservice.service.ReservationService;
import com.coherent.solutions.test.hotelreservationsservice.service.utils.DatesValidations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final String RESERVATION_NOT_FOUND = "reservation not found with id: %d";
    private final String ROOM_ALREADY_BOOKED = "room already booked for given dates";

    private final String RESERVATION_EXPIRED = "reservations in the past, can not be edited";
    private final ReservationRepository reservationRepository;
    @Override
    public ReservationResponseDTO saveReservation(ReservationRequestDTO reservationRequest) {

        validateRoomIsNotBookedForGivenDates(reservationRequest);

        Reservation reservation = ReservationMapper.INSTANCE.reservationRequestDTOToReservation(reservationRequest);
        reservation = reservationRepository.save(reservation);
        return ReservationMapper.INSTANCE.reservationToReservationResponseDTO(reservation);
    }

    @Override
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequest) {

        validateReservationExists(id);
        Reservation reservation = reservationRepository.findById(id).get();

        validateReservationIsNotInThePast(reservation);
        validateRoomIsNotBookedForGivenDates(reservationRequest);

        reservation = ReservationMapper.INSTANCE.reservationRequestDTOToReservation(reservationRequest);
        reservation.setId(id);

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

    @Override
    public void deleteReservation(int id) {
        validateReservationExists(id);
        reservationRepository.deleteById(id);
    }

    private void validateRoomIsNotBookedForGivenDates(ReservationRequestDTO reservationRequest) {
        if (!findReservationsWithOverlappingDates(reservationRequest).isEmpty()) {
            throw new BadRequestException(ROOM_ALREADY_BOOKED);
        }
    }
    private void validateReservationExists(int id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(RESERVATION_NOT_FOUND, id));
        }
    }

    private void validateReservationIsNotInThePast(Reservation reservation) {
        List<LocalDate> reservationDates = reservation.getReservationDates();
        LocalDate endDate = reservationDates.get(reservationDates.size() - 1);
        if (DatesValidations.isEndDateInThePast(endDate)) {
            throw new BadRequestException(RESERVATION_EXPIRED);
        }
    }
    private List<Reservation> findReservationsWithOverlappingDates(ReservationRequestDTO reservationRequest) {
        List<Reservation> reservations = reservationRepository
                .findByReservationByOverlappingDates(reservationRequest.getStartDate(), reservationRequest.getEndDate(),
                        reservationRequest.getRoomNumber());
        return reservations;
    }
}
