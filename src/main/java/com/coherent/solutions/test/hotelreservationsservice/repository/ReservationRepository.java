package com.coherent.solutions.test.hotelreservationsservice.repository;

import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

}
