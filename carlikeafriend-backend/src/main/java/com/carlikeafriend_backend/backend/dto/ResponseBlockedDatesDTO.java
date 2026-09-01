package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class ResponseBlockedDatesDTO {
    private List<LocalDate>  blockedDates;

    public ResponseBlockedDatesDTO(List<LocalDate> blockedDates) {
        this.blockedDates = blockedDates;
    }

    public ResponseBlockedDatesDTO() {
    }

    public List<LocalDate> getBlockedDates() {
        return blockedDates;
    }

    public void setBlockedDates(List<LocalDate> blockedDates) {
        this.blockedDates = blockedDates;
    }
}
