package com.capstone.workspace.helpers.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class LocalDateHelper {
    public static final String ZONE_ID = "Asia/Saigon";

    public Object getLocalTimeAtZoneRequest(String unit) {
        switch (unit) {
            case "date":
                return LocalDate.now(ZoneId.of(ZONE_ID));
            case "datetime":
                return LocalDateTime.now(ZoneId.of(ZONE_ID));
            default:
                return null;
        }
    }
}

