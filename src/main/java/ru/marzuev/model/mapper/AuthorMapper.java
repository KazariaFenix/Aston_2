package ru.marzuev.model.mapper;

import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.dto.AuthorDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorMapper {

    public static AuthorDto toAuthorDto(Author author, List<Book> authorBooks) {
        if (authorBooks == null) {
            authorBooks = new ArrayList<>();
        }
        return new AuthorDto(author.getName(), author.getDateBorn(),
                authorBooks.stream()
                        .map(book -> book.getTitle())
                        .collect(Collectors.toList()));
    }

    public static Author toAuthor(long id, AuthorDto authorDto) {
        return new Author(id, authorDto.getName(), authorDto.getDateBorn());
    }
}
