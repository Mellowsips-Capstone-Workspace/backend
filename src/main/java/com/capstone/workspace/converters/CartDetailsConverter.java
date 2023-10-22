package com.capstone.workspace.converters;

import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class CartDetailsConverter implements AttributeConverter<CartDetailsModel, String> {
    @NonNull
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(CartDetailsModel data) {
        try {
            return data == null ? null : objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public CartDetailsModel convertToEntityAttribute(String dbData) {
        try {
            return dbData == null || dbData.equals("null") ? null : objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

}
