package com.coherent.solutions.test.hotelreservationsservice.controller;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> saveReservation(@RequestBody ReservationRequestDTO reservationRequestDTO) {
        ReservationResponseDTO reservation = reservationService.saveReservation(reservationRequestDTO);
        return new ResponseEntity(reservation, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(@PathVariable int id
            , @RequestBody ReservationRequestDTO reservationRequestDTO) {
        ReservationResponseDTO reservation = reservationService.updateReservation(id, reservationRequestDTO);
        return new ResponseEntity(reservation, HttpStatus.ACCEPTED);
    }
}
