package com.coherent.solutions.test.hotelreservationsservice.service.utils;

import java.time.LocalDate;

public class DatesValidations {

    public static boolean areDatesValid(LocalDate startDate, LocalDate endDate) {
        return isStartDateNotInThePast(startDate) && isEndDateAfterStartDate(startDate, endDate);
    }

    private static boolean isEndDateAfterStartDate(LocalDate startDate, LocalDate endDate) {
        return endDate.isAfter(startDate);
    }

    private static boolean isStartDateNotInThePast(LocalDate startDate) {
        return !startDate.isBefore(LocalDate.now());
    }

    public static boolean isEndDateInThePast(LocalDate endDate) { return endDate.isBefore(LocalDate.now());}

}
