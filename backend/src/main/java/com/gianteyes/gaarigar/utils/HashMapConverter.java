package com.gianteyes.gaarigar.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        String objJson = null;
        try {
            objJson = objectMapper.writeValueAsString(obj);
        } catch (final JsonProcessingException e) {
            System.out.println("JSON writing error");
        }

        return objJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String objJSON) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> obj = null;
        try {
            obj = objectMapper.readValue(objJSON, Map.class);
        } catch (final IOException e) {
            System.out.println("JSON reading error");
        }

        return obj;
    }

}
