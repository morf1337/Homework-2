package org.example.mapper;

import org.example.dto.BookDto;
import org.example.entity.Book;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-23T17:32:05+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.22 (Ubuntu)"
)
*/
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto bookToBookDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDto bookDto = new BookDto();

        bookDto.setId( book.getId() );
        bookDto.setTitle( book.getTitle() );

        return bookDto;
    }
}
