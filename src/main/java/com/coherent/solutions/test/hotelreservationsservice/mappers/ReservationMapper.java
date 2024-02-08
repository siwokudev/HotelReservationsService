package com.coherent.solutions.test.hotelreservationsservice.mappers;

import com.coherent.solutions.test.hotelreservationsservice.dto.request.ReservationRequestDTO;
import com.coherent.solutions.test.hotelreservationsservice.dto.response.ReservationResponseDTO;
import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Mapper
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservationDates", expression = "java(mapDates(reservationRequest))")
    Reservation reservationRequestDTOToReservation(ReservationRequestDTO reservationRequest);

    ReservationResponseDTO reservationToReservationResponseDTO(Reservation reservation);

    default List<LocalDate> mapDates(ReservationRequestDTO value) {
        return Arrays.asList( value.getStartDate(),
                value.getEndDate());
    }
}
