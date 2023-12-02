package ru.marzuev.repository.impl;

import ru.marzuev.db.ConnectionManager;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.repository.AuthorRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorRepositoryImpl implements AuthorRepository {

    private ConnectionManager connectionManager;

    public AuthorRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Author addAuthor(Author author) throws SQLException {
        Author newAuthor = null;
        List<Book> books = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO authors (name, date_born) " +
                    "VALUES ( ?, ? )", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, author.getName());
            ps.setDate(2, Date.valueOf(author.getDateBorn()));
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Author Is Not Insert");
            }
            ResultSet rs = ps.getGeneratedKeys();
            long authorId = 0;
            while (rs.next()) {
                authorId = rs.getLong("author_id");
            }
            ps.close();

            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM authors WHERE author_id = ?");

            ps1.setLong(1, authorId);

            try (ResultSet authorResult = ps1.executeQuery()) {
                while (authorResult.next()) {
                    newAuthor = makeAuthor(authorResult);
                }
            }
        }
        newAuthor.setListBooks(books);

        return newAuthor;
    }

    @Override
    public Author updateAuthor(Author author) throws SQLException {
        Author newAuthor = null;
        List<Book> books = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE authors SET name = ?, date_born = ? " +
                    "WHERE author_id = ?");

            ps.setString(1, author.getName());
            ps.setDate(2, Date.valueOf(author.getDateBorn()));
            ps.setLong(3, author.getId());
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Author Is Not Update");
            }
            ps.close();
            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM authors WHERE author_id = ?");

            ps1.setLong(1, author.getId());
            try (ResultSet authorResult = ps1.executeQuery()) {
                while (authorResult.next()) {
                    newAuthor = makeAuthor(authorResult);
                }
            }
            ps1.close();
            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM books WHERE book_id IN (" +
                    "SELECT ab.book_id FROM authors_books AS ab WHERE author_id = ?)");

            ps2.setLong(1, author.getId());
            try (ResultSet booksResult = ps2.executeQuery()) {
                while (booksResult.next()) {
                    books.add(makeBook(booksResult));
                }
            }
            newAuthor.setListBooks(books);
        }

        return newAuthor;
    }

    @Override
    public void deleteAuthor(long authorId) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps1 = connection.prepareStatement("DELETE FROM books WHERE book_id IN (" +
                    "SELECT book_id FROM authors_books WHERE author_id = ?)");

            ps1.setLong(1, authorId);
            ps1.executeUpdate();
            ps1.close();

            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM authors WHERE author_id = ?");

            ps2.setLong(1, authorId);
            int count = ps2.executeUpdate();

            if (count == 0) {
                throw new SQLException("Author Is Not Delete");
            }
            ps2.close();


        }
    }

    @Override
    public Author getAuthorById(long authorId) throws SQLException {
        Author findAuthor = null;
        List<Book> books = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM authors WHERE author_id = ?");

            statement.setLong(1, authorId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    findAuthor = makeAuthor(results);
                }
            }
            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM books WHERE book_id IN (" +
                    "SELECT ab.book_id FROM authors_books AS ab WHERE author_id = ?)");

            ps2.setLong(1, authorId);
            try (ResultSet booksResult = ps2.executeQuery()) {
                while (booksResult.next()) {
                    books.add(makeBook(booksResult));
                }
            }
        }
        if (findAuthor == null) {
            throw new IllegalArgumentException("Author Not Found");
        } else {
            findAuthor.setListBooks(books);
            return findAuthor;
        }
    }

    @Override
    public Map<Author, List<Book>> getAuthorsWithBooks() throws SQLException {
        Map<Author, List<Book>> authorWithBook = new HashMap<>();
        try (Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM authors AS a LEFT JOIN authors_books AS ab " +
                    "ON a.author_id = ab.author_id LEFT JOIN books AS b ON ab.book_id = b.book_id ORDER BY a.author_id");

            while (resultSet.next()) {
                Author author = makeAuthor(resultSet);
                Book book = null;
                if (resultSet.getString("book_id") != null) {
                    book = makeBook(resultSet);
                }
                List<Book> booksByAuthor = new ArrayList<>();
                if (book == null) {
                    authorWithBook.put(author, booksByAuthor);
                    continue;
                }
                if (authorWithBook.containsKey(author)) {
                    authorWithBook.get(author).add(book);
                } else {
                    booksByAuthor.add(book);
                    authorWithBook.put(author, booksByAuthor);
                }
            }
        }

        return authorWithBook;
    }

    private Author makeAuthor(ResultSet rs) throws SQLException {
        return new Author(rs.getLong("author_id"), rs.getString("name"),
                rs.getObject("date_born", LocalDate.class));
    }

    private Book makeBook(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("book_id"), rs.getString("title"),
                rs.getString("description"), rs.getObject("release", LocalDate.class));
    }
}
