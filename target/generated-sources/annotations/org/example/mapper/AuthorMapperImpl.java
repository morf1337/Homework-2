package org.example.mapper;

import org.example.dto.AuthorDto;
import org.example.entity.Author;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-23T17:32:05+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.22 (Ubuntu)"
)
*/
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public AuthorDto authorToAuthorDto(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorDto authorDto = new AuthorDto();

        authorDto.setId( author.getId() );
        authorDto.setName( author.getName() );

        return authorDto;
    }
}
