package org.example.mapper;

import org.example.dto.AuthorDto;
import org.example.dto.BookDto;
import org.example.entity.Author;
import org.example.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
    BookDto bookToBookDto(Book book);
}
