package com.capstone.workspace.converters;

import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.capstone.workspace.models.store.QrCodeModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class QrCodeConverter implements AttributeConverter<QrCodeModel, String> {
    @NonNull
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(QrCodeModel data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public QrCodeModel convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

}
