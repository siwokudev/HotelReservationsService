package com.coherent.solutions.test.hotelreservationsservice.service;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.BadRequestException;
import com.coherent.solutions.test.hotelreservationsservice.exceptions.ResourceNotFoundException;
import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.dto.ReservationRequestDTOMother;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.model.ReservationMother;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.utils.DatesMother;
import com.coherent.solutions.test.hotelreservationsservice.repository.ReservationRepository;
import com.coherent.solutions.test.hotelreservationsservice.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    @Mock
    private ReservationRepository reservationRepository;
    @Test
    public void shouldFailWhenSavingAReservationAndTheRoomIsAlreadyBookedForTheGivenDates() {

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .build();

        when(reservationRepository.findByReservationAndByOverlappingDates(any(LocalDate.class), any(LocalDate.class), anyInt()))
                .thenReturn(
                        Arrays.asList(ReservationMother.complete().build()));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.saveReservation(request);
        });

        assertEquals("room already booked for given dates", exception.getMessage());

        verify(reservationRepository).findByReservationAndByOverlappingDates(any(LocalDate.class), any(LocalDate.class), anyInt());
    }

    @Test
    public void shouldSucceedWhenSavingAReservationAndTheRoomIsNotBooked() {
        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .build();

        when(reservationRepository.findByReservationAndByOverlappingDates(any(LocalDate.class), any(LocalDate.class), anyInt()))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).then(AdditionalAnswers.returnsFirstArg());

        ReservationResponseDTO response = reservationServiceImpl.saveReservation(request);

        assertEquals(request.getRoomNumber(), response.getRoomNumber());
        assertEquals(request.getClientFullName(), response.getClientFullName());
        assertTrue(response.getReservationDates().contains(request.getStartDate()));
        assertTrue(response.getReservationDates().contains(request.getEndDate()));

        verify(reservationRepository).findByReservationAndByOverlappingDates(any(LocalDate.class), any(LocalDate.class), anyInt());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationDoesNotExists() {
        int id = 123;
        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .build();

        when(reservationRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals(String.format("reservation not found with id: %d", id), exception.getMessage());

        verify(reservationRepository).existsById(id);
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationIsInThePast() {
        int id = 123;

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .build();

        Reservation reservationSaved = ReservationMother.inThePast()
                .id(id)
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationSaved));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals("reservations in the past, can not be edited", exception.getMessage());

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationIsAlreadyBookedForTheGivenDatesWithContainedDates() {
        /*
        bookedReservationDate                 Start |-------------|-------------| End
        requestSDates           Start |-------------|-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(4, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.plusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.minusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation reservationToUpdate = ReservationMother.fromRequest(request)
                .id(id)
                .build();

        Reservation reservationWithSameDatesAsRequest = ReservationMother.fromRequest(request)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationToUpdate));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(reservationWithSameDatesAsRequest));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals("room already booked for given dates", exception.getMessage());

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationIsAlreadyBookedForTheGivenDatesWithContainingDates() {
        /*
        bookedReservationDate        Start |-------------|-------------|-------------|-------------| End
        requestSDates                              Start |-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(2, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.minusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation reservationToUpdate = ReservationMother.fromRequest(request)
                .id(id)
                .build();

        Reservation reservationWithSameDatesAsRequest = ReservationMother.fromRequest(request)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationToUpdate));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(reservationWithSameDatesAsRequest));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals("room already booked for given dates", exception.getMessage());

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationIsAlreadyBookedForTheGivenDatesWithOverlappingStartDate() {
        /*
        bookedReservationDate                      Start |-------------|-------------| End
        requestSDates  Start |-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(3, requestStartDate);

        LocalDate savedReservationStartDate = requestEndDate.minusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation reservationToUpdate = ReservationMother.fromRequest(request)
                .id(id)
                .build();

        Reservation reservationWithSameDatesAsRequest = ReservationMother.fromRequest(request)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationToUpdate));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(reservationWithSameDatesAsRequest));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals("room already booked for given dates", exception.getMessage());

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldFailWhenUpdatingAReservationAndTheReservationIsAlreadyBookedForTheGivenDatesWithOverlappingEndDate() {
        /*
        bookedReservationDate        Start |-------------|-------------| End
        requestSDates                              Start |-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(3, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.minusDays(1);
        LocalDate savedReservationEndDate = requestStartDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation reservationToUpdate = ReservationMother.fromRequest(request)
                .id(id)
                .build();

        Reservation reservationWithSameDatesAsRequest = ReservationMother.fromRequest(request)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservationToUpdate));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(reservationWithSameDatesAsRequest));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            reservationServiceImpl.updateReservation(id, request);
        });

        assertEquals("room already booked for given dates", exception.getMessage());

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldPassWhenUpdatingAReservationAndIsTheSameReservationWithContainedDates() {
        /*
        bookedReservationDate                 Start |-------------|-------------| End
        requestSDates           Start |-------------|-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(4, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.plusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.minusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation savedReservation = ReservationMother.fromRequest(request)
                .id(id)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).then(AdditionalAnswers.returnsFirstArg());

        ReservationResponseDTO response = reservationServiceImpl.updateReservation(id, request);

        assertEquals(savedReservation.getId(), response.getId());
        assertEquals(request.getRoomNumber(), response.getRoomNumber());
        assertEquals(request.getClientFullName(), response.getClientFullName());
        assertTrue(response.getReservationDates().contains(request.getStartDate()));
        assertTrue(response.getReservationDates().contains(request.getEndDate()));

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldPassWhenUpdatingAReservationAndIsTheSameReservationWithContainingDates() {
         /*
        bookedReservationDate        Start |-------------|-------------|-------------|-------------| End
        requestSDates                              Start |-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(2, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.minusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation savedReservation = ReservationMother.fromRequest(request)
                .id(id)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).then(AdditionalAnswers.returnsFirstArg());

        ReservationResponseDTO response = reservationServiceImpl.updateReservation(id, request);

        assertEquals(savedReservation.getId(), response.getId());
        assertEquals(request.getRoomNumber(), response.getRoomNumber());
        assertEquals(request.getClientFullName(), response.getClientFullName());
        assertTrue(response.getReservationDates().contains(request.getStartDate()));
        assertTrue(response.getReservationDates().contains(request.getEndDate()));

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldPassWhenUpdatingAReservationAndIsTheSameReservationWithOverlappingEndDate() {
        /*
        bookedReservationDate        Start |-------------|-------------| End
        requestSDates                              Start |-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(3, requestStartDate);

        LocalDate savedReservationStartDate = requestStartDate.minusDays(1);
        LocalDate savedReservationEndDate = requestStartDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation savedReservation = ReservationMother.fromRequest(request)
                .id(id)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).then(AdditionalAnswers.returnsFirstArg());

        ReservationResponseDTO response = reservationServiceImpl.updateReservation(id, request);

        assertEquals(savedReservation.getId(), response.getId());
        assertEquals(request.getRoomNumber(), response.getRoomNumber());
        assertEquals(request.getClientFullName(), response.getClientFullName());
        assertTrue(response.getReservationDates().contains(request.getStartDate()));
        assertTrue(response.getReservationDates().contains(request.getEndDate()));

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldPassWhenUpdatingAReservationAndIsTheSameReservationWithOverlappingStartDate() {
        /*
        bookedReservationDate                      Start |-------------|-------------| End
        requestSDates  Start |-------------|-------------|-------------| End
        */

        int id = 123;

        LocalDate requestStartDate = DatesMother.inTheFuture();
        LocalDate requestEndDate = DatesMother.inTheFutureFromDateAndFixedDaysApart(3, requestStartDate);

        LocalDate savedReservationStartDate = requestEndDate.minusDays(1);
        LocalDate savedReservationEndDate = requestEndDate.plusDays(1);

        ReservationRequestDTO request = ReservationRequestDTOMother.complete()
                .startDate(requestStartDate)
                .endDate(requestEndDate)
                .build();

        Reservation savedReservation = ReservationMother.fromRequest(request)
                .id(id)
                .reservationDates(Arrays.asList(savedReservationStartDate, savedReservationEndDate))
                .build();

        when(reservationRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.findByReservationAndByOverlappingDates(requestStartDate, requestEndDate, request.getRoomNumber()))
                .thenReturn(Arrays.asList(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).then(AdditionalAnswers.returnsFirstArg());

        ReservationResponseDTO response = reservationServiceImpl.updateReservation(id, request);

        assertEquals(savedReservation.getId(), response.getId());
        assertEquals(request.getRoomNumber(), response.getRoomNumber());
        assertEquals(request.getClientFullName(), response.getClientFullName());
        assertTrue(response.getReservationDates().contains(request.getStartDate()));
        assertTrue(response.getReservationDates().contains(request.getEndDate()));

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    public void shouldReturnAListOfReservations() {
        List<Reservation> reservations = Arrays.asList(ReservationMother.inThePast().build(), ReservationMother.complete().build());

        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ReservationResponseDTO> response = reservationServiceImpl.getReservations();

        assertTrue(!response.isEmpty());
        assertEquals(2, response.size());

        verify(reservationRepository).findAll();

    }

    @Test
    public void shouldFailWhenDeletingANonExistentReservation() {
        int id = 123;

        when(reservationRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
           reservationServiceImpl.deleteReservation(id);
        });

        assertEquals(String.format("reservation not found with id: %d", id), exception.getMessage());

        verify(reservationRepository).existsById(id);

    }

    @Test
    public void shouldPassWhenDeletingAExistingReservation() {
        int id = 123;

        when(reservationRepository.existsById(id)).thenReturn(true);

        reservationServiceImpl.deleteReservation(id);

        verify(reservationRepository).existsById(id);
        verify(reservationRepository).deleteById(id);

    }


}
