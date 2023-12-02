package ru.marzuev.model.mapper;

import ru.marzuev.model.Author;
import ru.marzuev.model.dto.AuthorDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AuthorMapper {

    public static AuthorDto toAuthorDto(Author author) {
        if (author.getListBooks() == null) {
            author.setListBooks(new ArrayList<>());
        }

        return new AuthorDto(author.getName(), author.getDateBorn(),
                author.getListBooks().stream()
                        .map(book -> book.getTitle())
                        .collect(Collectors.toList()));
    }

    public static Author toAuthor(long id, AuthorDto authorDto) {
        return new Author(id, authorDto.getName(), authorDto.getDateBorn());
    }
}
