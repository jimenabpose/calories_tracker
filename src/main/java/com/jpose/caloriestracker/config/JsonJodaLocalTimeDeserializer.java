package com.jpose.caloriestracker.config;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonJodaLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
	
	@Override
    public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            return JacksonConfiguration.TIME_FORMATTER.parseLocalTime(str);
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new LocalTime(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
    }
}