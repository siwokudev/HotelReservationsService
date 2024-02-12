package com.coherent.solutions.test.hotelreservationsservice.controller;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.dto.request.ReservationRequestDTOMother;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.dto.response.ReservationResponseDTOMother;
import com.coherent.solutions.test.hotelreservationsservice.objectmother.utils.DatesMother;
import com.coherent.solutions.test.hotelreservationsservice.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ReservationController.class)
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    String path = "/reservation";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldFailWhenRequestHasNullClientFullName() throws Exception {
        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .clientFullName(null)
                .build();

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("clientFullName must not be null", "clientFullName must not be blank");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void shouldFailWhenRequestHasEmptyClientFullName() throws Exception {
        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .clientFullName("")
                .build();

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("clientFullName must not be blank");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void shouldFailWhenRequestHasInvalidRoomNumber() throws Exception {
        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .roomNumber(0)
                .build();

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("roomNumber must be greater than 0");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void shouldFailWhenRequestHasInvalidDates_StartDateInThePast() throws Exception {
        LocalDate startDate = DatesMother.inThePast();

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .startDate(startDate)
                .build();

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("Invalid date range");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void shouldFailWhenRequestHasInvalidDates_EndDateBeforeStartDate() throws Exception {
        LocalDate startDate = DatesMother.inTheFuture();

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .startDate(startDate)
                .endDate(startDate.minusDays(1))
                .build();

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("Invalid date range");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void shouldPassWhenSavingAReservation() throws Exception {
        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete().build();

        ReservationResponseDTO responseDTO = ReservationResponseDTOMother.complete().build();
        when(reservationService.saveReservation(any())).thenReturn(responseDTO);

        MvcResult response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        String expectedResponse = objectMapper.writeValueAsString(responseDTO);

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getResponse().getContentAsString()).isEqualTo(expectedResponse);

        verify(reservationService).saveReservation(any());
    }

    @Test
    void shouldGetAListOfReservations() throws Exception {
        ReservationResponseDTO reservationResponseDTO = ReservationResponseDTOMother.complete().build();

        when(reservationService.getReservations()).thenReturn(Arrays.asList(reservationResponseDTO));

        MvcResult response = mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String expectedResponse = objectMapper.writeValueAsString(Arrays.asList(reservationResponseDTO));

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getResponse().getContentAsString()).isEqualTo(expectedResponse);

        verify(reservationService).getReservations();
    }

    @Test
    void shouldFailWhenUpdatingAReservationAndRequestHasNullClientFullName() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .clientFullName(null)
                .build();

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("clientFullName must not be null", "clientFullName must not be blank");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldFailWhenUpdatingAReservationAndRequestHasEmptyClientFullName() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .clientFullName("")
                .build();

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("clientFullName must not be blank");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldFailWhenUpdatingAReservationAndRequestHasInvalidRoomNumber() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .roomNumber(0)
                .build();

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("roomNumber must be greater than 0");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldFailWhenUpdatingAReservationAndRequestHasInvalidDates_StartDateInThePast() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);
        LocalDate startDate = DatesMother.inThePast();

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .startDate(startDate)
                .build();

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("Invalid date range");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldFailWhenUpdatingAReservationAndRequestHasInvalidDates_EndDateBeforeStartDate() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);
        LocalDate startDate = DatesMother.inTheFuture();

        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete()
                .startDate(startDate)
                .endDate(startDate.minusDays(1))
                .build();

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        List<String> expectedResponse = Arrays.asList("Invalid date range");

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains(expectedResponse);

        verify(reservationService, never()).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldPassWhenUpdatingAReservation() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);
        ReservationRequestDTO requestDTO = ReservationRequestDTOMother.complete().build();

        ReservationResponseDTO responseDTO = ReservationResponseDTOMother.complete().build();
        when(reservationService.updateReservation(id, requestDTO)).thenReturn(responseDTO);

        MvcResult response = mockMvc.perform(put(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();

        String expectedResponse = objectMapper.writeValueAsString(responseDTO);

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(response.getResponse().getContentAsString()).isEqualTo(expectedResponse);

        verify(reservationService).updateReservation(anyInt(), any(ReservationRequestDTO.class));
    }

    @Test
    void shouldDeleteAReservation() throws Exception {
        int id = Faker.instance().number().numberBetween(1, 999);

        MvcResult response = mockMvc.perform(delete(path.concat("/{id}"), id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());

        verify(reservationService).deleteReservation(id);
    }
}
