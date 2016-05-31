package com.jpose.caloriestracker.config;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonJodaLocalDateDeserializer extends JsonDeserializer<LocalDate> {
	
    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            return JacksonConfiguration.DATE_FORMATTER.parseLocalDate(str);
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new LocalDate(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
    }
}