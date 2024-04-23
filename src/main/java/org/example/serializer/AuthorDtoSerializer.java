package org.example.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.dto.AuthorDto;
import java.io.IOException;

public class AuthorDtoSerializer extends JsonSerializer<AuthorDto> {

    @Override
    public void serialize(AuthorDto authorDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", authorDto.getId());
        jsonGenerator.writeStringField("name", authorDto.getName());
        jsonGenerator.writeEndObject();
    }
}