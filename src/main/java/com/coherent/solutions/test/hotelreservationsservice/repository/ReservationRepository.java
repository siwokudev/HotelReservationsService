package com.coherent.solutions.test.hotelreservationsservice.repository;

import com.coherent.solutions.test.hotelreservationsservice.model.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {
    List<Reservation> findAll();

    @Query(value = """
        with dates (id, start_date, end_date) as (
            select 
            reservation_id,
            min(reservation_date) as start_date,
            max(reservation_date) as end_date
            from reservation_dates
            group by reservation_id
        ) 
        select 
        r.id
        ,r.client_full_name
        ,r.room_number
        ,dates.start_date
        ,dates.end_date
        from reservation as r
        inner join dates on r.id = dates.id
        where dates.end_date > ?1
        and dates.start_date < ?2
        and r.room_number = ?3
        """, nativeQuery = true)
    List<Reservation> findByReservationByOverlappingDates(LocalDate startDate, LocalDate endDate, int roomNumber);
}
