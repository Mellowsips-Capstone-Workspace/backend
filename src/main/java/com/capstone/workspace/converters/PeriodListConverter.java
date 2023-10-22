package com.capstone.workspace.converters;

import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.capstone.workspace.models.shared.Period;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Converter
@RequiredArgsConstructor
public class PeriodListConverter<T> implements AttributeConverter<Map<DayOfWeek, List<Period<T>>>, String> {
    @NonNull
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<DayOfWeek, List<Period<T>>> periods) {
        try {
            return periods == null ? null : objectMapper.writeValueAsString(periods);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public Map<DayOfWeek, List<Period<T>>> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null || dbData.equals("null") ? null : objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

}
