package com.coherent.solutions.test.hotelreservationsservice.controller;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.BadRequestException;
import com.coherent.solutions.test.hotelreservationsservice.service.ReservationService;
import com.coherent.solutions.test.hotelreservationsservice.service.validations.DatesValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final String INVALID_DATE_RANGE = "Invalid date range";
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> saveReservation(@RequestBody @Valid ReservationRequestDTO reservationRequestDTO) {

        validateDates(reservationRequestDTO);

        ReservationResponseDTO reservation = reservationService.saveReservation(reservationRequestDTO);
        return new ResponseEntity(reservation, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(@PathVariable int id
            , @RequestBody  @Valid ReservationRequestDTO reservationRequestDTO) {

        validateDates(reservationRequestDTO);

        ReservationResponseDTO reservation = reservationService.updateReservation(id, reservationRequestDTO);
        return new ResponseEntity(reservation, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private void validateDates(ReservationRequestDTO reservationRequestDTO) {
        if (!DatesValidation.areDatesValid(reservationRequestDTO.getStartDate(), reservationRequestDTO.getEndDate())) {
            throw new BadRequestException(INVALID_DATE_RANGE);
        }
    }
}
