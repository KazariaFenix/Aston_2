package ru.marzuev.service;

import ru.marzuev.model.dto.AuthorDto;

import java.sql.SQLException;
import java.util.List;

public interface AuthorService {

    AuthorDto addAuthor(AuthorDto authorDto) throws SQLException;

    AuthorDto updateAuthor(AuthorDto authorDto, long authorId) throws SQLException;

    void deleteAuthor(long authorId) throws SQLException;

    AuthorDto getAuthorById(long authorId) throws SQLException;

    List<AuthorDto> getAuthors() throws SQLException;
}
