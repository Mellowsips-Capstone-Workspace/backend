package com.capstone.workspace.helpers.shared;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class LocalDateHelper {
    @NonNull
    private final HttpServletRequest request;

    public Object getLocalTimeAtZoneRequest(String unit) {
        String zoneId = request.getHeader("Zone");

        switch (unit) {
            case "date":
                if (hasZone(zoneId)) {
                    return LocalDate.now(ZoneId.of(zoneId));
                }
                return LocalDate.now();
            case "datetime":
                if (hasZone(zoneId)) {
                    return LocalDateTime.now(ZoneId.of(zoneId));
                }
                return LocalDateTime.now();
            default:
                return null;
        }
    }

    private boolean hasZone(String zoneId) {
        return zoneId != null && ZoneId.getAvailableZoneIds().contains(zoneId);
    }
}

