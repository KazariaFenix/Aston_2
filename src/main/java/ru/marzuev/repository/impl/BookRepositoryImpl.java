package ru.marzuev.repository.impl;

import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.Book;
import ru.marzuev.repository.BookRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookRepositoryImpl implements BookRepository {
    ConnectionManager connectionManager;

    public BookRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Book addBook(Book book, List<Long> authorsList) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO books (title, description, release) " +
                "VALUES ( ?, ?, ? )", Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, book.getTitle());
        ps.setString(2, book.getDescription());
        ps.setDate(3, Date.valueOf(book.getRelease()));
        int count = ps.executeUpdate();

        if (count == 0) {
            throw new SQLException("Book Is Not Insert");
        }
        ResultSet rs = ps.getGeneratedKeys();
        long bookId = 0;
        while (rs.next()) {
            bookId = rs.getLong("book_id");
        }
        ps.close();

        for (Long authorId : authorsList) {
            PreparedStatement ps1 = connection.prepareStatement("INSERT INTO authors_books (author_id, book_id)" +
                    "VALUES ( ?, ? )");

            ps1.setLong(1, authorId);
            ps1.setLong(2, bookId);
            int countRow = ps1.executeUpdate();

            if (countRow == 0) {
                throw new SQLException("Author and Book Is Not Insert");
            }
            ps1.close();
        }

        PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM books WHERE book_id = ?");

        ps2.setLong(1, bookId);
        Book newBook = null;

        try (ResultSet bookResult = ps2.executeQuery()) {
            while (bookResult.next()) {
                newBook = makeBook(bookResult);
            }
        }
        connection.close();

        return newBook;
    }

    @Override
    public Book updateBook(Book book) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE books " +
                "SET title = ?, description = ?, release = ? WHERE book_id = ?");

        ps.setString(1, book.getTitle());
        ps.setString(2, book.getDescription());
        ps.setDate(3, Date.valueOf(book.getRelease()));
        ps.setLong(4, book.getId());
        int count = ps.executeUpdate();

        if (count == 0) {
            throw new SQLException("Book Is Not Update");
        }
        ps.close();

        PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM books WHERE book_id = ?");

        ps2.setLong(1, book.getId());
        Book newBook = null;

        try (ResultSet bookResult = ps2.executeQuery()) {
            while (bookResult.next()) {
                newBook = makeBook(bookResult);
            }
        }
        connection.close();

        return newBook;
    }

    @Override
    public void deleteBook(long bookId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement ps = connection.prepareStatement("DELETE FROM books WHERE book_id = ?");

        ps.setLong(1, bookId);
        int count = ps.executeUpdate();

        if (count == 0) {
            throw new SQLException("Book Is Not Delete");
        }
        ps.close();

        PreparedStatement ps1 = connection.prepareStatement("DELETE FROM authors_books WHERE book_id = ?");

        ps1.setLong(1, bookId);
        ps1.executeUpdate();
        ps1.close();
        connection.close();
    }

    @Override
    public Book getBookById(long bookId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE book_id = ?");

        statement.setLong(1, bookId);
        Book findBook = null;
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                findBook = makeBook(results);
            }
        }
        connection.close();
        if (findBook == null) {
            throw new IllegalArgumentException();
        } else {
            return findBook;
        }
    }

    @Override
    public List<Book> getBooksByAuthorId(long authorId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM books " +
                "WHERE book_id IN (SELECT book_id FROM authors_books WHERE author_id = ?)");

        statement.setLong(1, authorId);
        List<Book> listBook = new ArrayList<>();

        statement.setObject(1, authorId);
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                listBook.add(makeBook(rs));
            }
        }
        connection.close();

        return listBook;
    }


    @Override
    public List<Long> findAuthorByBookId(long bookId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM authors_books WHERE book_id = ?");

        statement.setLong(1, bookId);
        List<Long> authorsId = new ArrayList<>();
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                authorsId.add(results.getLong("author_id"));
            }
        }
        connection.close();
        if (authorsId.size() == 0) {
            throw new IllegalArgumentException();
        } else {
            return authorsId;
        }
    }

    private Book makeBook(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("book_id"), rs.getString("title"),
                rs.getString("description"), rs.getObject("release", LocalDate.class));
    }
}
