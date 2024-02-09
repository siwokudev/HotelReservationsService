package com.coherent.solutions.test.hotelreservationsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "reservation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "client_full_name", nullable = false)
    private String clientFullName;

    @Column(name = "room_number", nullable = false)
    private int roomNumber;

    //refer to https://medium.com/@saeiddrv/jpa-hibernate-mapping-types-891686bc6cfd
    @ElementCollection
    @Column(name = "reservation_date")
    @CollectionTable(
            name = "reservation_dates",
            joinColumns = @JoinColumn(name="reservation_id"))
    private List<LocalDate> reservationDates;
    /*
    improvement: change this to be startDate and EndDate will make it easier to find by date ranges
      LocalDate startDate;
      LocalDate endDate;
    */
}
