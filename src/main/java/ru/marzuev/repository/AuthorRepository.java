package ru.marzuev.repository;

import ru.marzuev.model.Author;
import ru.marzuev.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AuthorRepository {

    Author addAuthor(Author author) throws SQLException;

    Author updateAuthor(Author author) throws SQLException;

    void deleteAuthor(long authorId) throws SQLException;

    Author getAuthorById(long authorId) throws SQLException;

    Map<Author, List<Book>> getAuthorsWithBooks() throws SQLException;
}
