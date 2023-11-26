package ru.marzuev.service;

import ru.marzuev.model.dto.BookDto;

import java.sql.SQLException;
import java.util.List;

public interface BookService {

    BookDto addBook(BookDto bookDto, List<Long> authorsList) throws SQLException;

    BookDto updateBook(BookDto bookDto, long bookId) throws SQLException;

    void deleteBook(long bookId) throws SQLException;

    BookDto getBookById(long bookId) throws SQLException;

    List<BookDto> getBooksByAuthorId(long authorId) throws SQLException;
}
