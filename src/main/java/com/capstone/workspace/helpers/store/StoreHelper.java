package com.capstone.workspace.helpers.store;

import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.helpers.shared.LocalDateHelper;
import com.capstone.workspace.models.shared.Period;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StoreHelper {
    private static Logger logger = LoggerFactory.getLogger(StoreHelper.class);

    public Boolean isStoreOpening(Map<DayOfWeek, List<Period>> operationalHours) {
        try {
            if (operationalHours == null) {
                return false;
            }

            LocalDateTime now = (LocalDateTime) BeanHelper.getBean(LocalDateHelper.class).getLocalTimeAtZoneRequest("datetime");
            DayOfWeek dayOfWeekNow = now.getDayOfWeek();

            List<Period> activeHours = operationalHours.get(dayOfWeekNow);
            if (activeHours == null) {
                return false;
            }

            SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
            Date timeNow = parser.parse(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());

            return activeHours.stream().anyMatch(item -> {
                Date start = null;
                Date end = null;
                try {
                    start = parser.parse(item.getStart().toString() + ":00");
                    end = parser.parse(item.getEnd().toString() + ":00");
                    return timeNow.after(start) && timeNow.before(end);
                } catch (ParseException e) {
                    logger.warn(e.getMessage());
                    return false;
                }
            });
        } catch (ParseException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }
}
