package ru.marzuev.repository;

import ru.marzuev.model.Author;
import ru.marzuev.model.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookRepository {

    Book addBook(Book book, List<Long> authorsList) throws SQLException;

    Book updateBook(Book book) throws SQLException;

    void deleteBook(long bookId) throws SQLException;

    Book getBookById(long bookId) throws SQLException;

    List<Author> findAuthorByBookId(long bookId) throws SQLException;

    List<Book> getBooksByAuthorId(long authorId) throws SQLException;
}
