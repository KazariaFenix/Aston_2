package ru.marzuev.repository.impl;

import ru.marzuev.db.ConnectionManager;
import ru.marzuev.model.Author;
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
        Book newBook = null;

        try (Connection connection = connectionManager.getConnection()) {
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
            try (ResultSet bookResult = ps2.executeQuery()) {
                while (bookResult.next()) {
                    newBook = makeBook(bookResult);
                }
            }
            ps2.close();
        }

        return newBook;
    }

    @Override
    public Book updateBook(Book book) throws SQLException {
        Book newBook = null;

        try (Connection connection = connectionManager.getConnection()) {
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
            try (ResultSet bookResult = ps2.executeQuery()) {
                while (bookResult.next()) {
                    newBook = makeBook(bookResult);
                }
            }
        }

        return newBook;
    }

    @Override
    public void deleteBook(long bookId) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM books WHERE book_id = ?");

            ps.setLong(1, bookId);
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Book Is Not Delete");
            }
            ps.close();
        }
    }

    @Override
    public Book getBookById(long bookId) throws SQLException {
        Book findBook = null;

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE book_id = ?");

            statement.setLong(1, bookId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    findBook = makeBook(results);
                }
            }
        }
        if (findBook == null) {
            throw new IllegalArgumentException();
        } else {
            return findBook;
        }
    }

    @Override
    public List<Book> getBooksByAuthorId(long authorId) throws SQLException {
        List<Book> listBook = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM books " +
                    "WHERE book_id IN (SELECT ab.book_id FROM authors_books AS ab WHERE ab.author_id = ?)");

            statement.setLong(1, authorId);
            statement.setObject(1, authorId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    listBook.add(makeBook(rs));
                }
            }
        }

        return listBook;
    }


    @Override
    public List<Author> findAuthorByBookId(long bookId) throws SQLException {
        List<Author> authorsId = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM authors WHERE author_id IN " +
                    "(SELECT ab.author_id FROM authors_books AS ab WHERE ab.book_id = ?)");

            statement.setLong(1, bookId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    authorsId.add(makeAuthor(results));
                }
            }
        }
        if (authorsId.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            return authorsId;
        }
    }

    private Book makeBook(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("book_id"), rs.getString("title"),
                rs.getString("description"), rs.getObject("release", LocalDate.class));
    }

    private Author makeAuthor(ResultSet rs) throws SQLException {
        return new Author(rs.getLong("author_id"), rs.getString("name"),
                rs.getObject("date_born", LocalDate.class));
    }
}
