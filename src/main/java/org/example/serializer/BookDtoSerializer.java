package org.example.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.dto.BookDto;
import java.io.IOException;

public class BookDtoSerializer extends JsonSerializer<BookDto> {

    @Override
    public void serialize(BookDto bookDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", bookDto.getId());
        jsonGenerator.writeStringField("title", bookDto.getTitle());
        jsonGenerator.writeEndObject();
    }
}
