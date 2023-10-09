package com.capstone.workspace.helpers.application;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class LocalDateHelper {
    @NonNull
    private final HttpServletRequest request;

    public LocalDate getLocalDateAtZoneRequest() {
        String zoneId = request.getHeader("Zone");
        if(zoneId != null && ZoneId.getAvailableZoneIds().contains(zoneId)){
            return LocalDate.now(ZoneId.of(zoneId));
        }
        return LocalDate.now();
    }
}

