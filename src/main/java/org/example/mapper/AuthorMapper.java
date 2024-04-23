package org.example.mapper;

import org.example.dto.AuthorDto;
import org.example.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);
    AuthorDto authorToAuthorDto(Author author);
}
