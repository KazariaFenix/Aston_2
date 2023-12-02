package ru.marzuev.repository.impl;

import ru.marzuev.db.ConnectionManager;
import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.repository.CommentRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRepositoryImpl implements CommentRepository {
    ConnectionManager connectionManager;

    public CommentRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Comment addComment(Comment comment, long bookId) throws SQLException {
        Comment postComment = null;

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO comments (content, book_id) " +
                    "VALUES ( ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, comment.getContent());
            ps.setLong(2, bookId);
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Comment Is Not Insert");
            }
            long commentId = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                while (rs.next()) {
                    commentId = rs.getLong("comment_id");
                }
            }
            ps.close();
            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");

            ps2.setLong(1, commentId);
            try (ResultSet rs = ps2.executeQuery()) {
                while (rs.next()) {
                    postComment = makeComment(rs);
                }
            }
        }

        return postComment;
    }

    @Override
    public Comment updateComment(Comment comment, long commentId) throws SQLException {
        Comment updateComment = null;

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE comments SET content = ? " +
                    "WHERE comment_id = ?");

            ps.setString(1, comment.getContent());
            ps.setLong(2, commentId);
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Comment Is Not Update");
            }

            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");
            ps2.setLong(1, commentId);
            try (ResultSet rs = ps2.executeQuery()) {
                while (rs.next()) {
                    updateComment = makeComment(rs);
                }
            }
            ps2.close();
        }
        updateComment.setBook(comment.getBook());
        return updateComment;
    }

    @Override
    public void deleteComment(long commentId) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM comments WHERE comment_id = ?");

            ps.setLong(1, commentId);
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Comment Is Not Update");
            }
        }
    }

    @Override
    public Comment getCommentById(long commentId) throws SQLException {
        Comment findComment = null;
        Book book = null;

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");

            statement.setLong(1, commentId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    findComment = makeComment(results);
                }
            }

            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM books WHERE book_id IN " +
                    "(SELECT c.book_id FROM comments AS c WHERE c.comment_id = ?)");

            ps1.setLong(1, commentId);
            try (ResultSet rs = ps1.executeQuery()) {
                while (rs.next()) {
                    book = makeBook(rs);
                }
            }
        }
        if (findComment == null) {
            throw new IllegalArgumentException();
        } else {
            findComment.setBook(book);
            return findComment;
        }
    }

    @Override
    public List<Comment> getCommentsByBookId(long bookId) throws SQLException {
        List<Comment> bookComments = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments WHERE book_id = ?");

            statement.setLong(1, bookId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    bookComments.add(makeComment(results));
                }
            }
        }

        return bookComments;
    }

    @Override
    public Map<Long, List<Comment>> getCommentByBookByAuthorId(long authorId) throws SQLException {
        Map<Long, List<Comment>> booksCommentsByAuthor = new HashMap<>();

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments WHERE book_id " +
                    "IN (SELECT ab.book_id FROM authors_books AS ab WHERE ab.author_id = ?)");

            statement.setLong(1, authorId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Comment comment = makeComment(results);
                    long bookId = results.getLong("book_id");
                    if (booksCommentsByAuthor.containsKey(bookId)) {
                        booksCommentsByAuthor.get(bookId).add(comment);
                    } else {
                        List<Comment> bookComments = new ArrayList<>();
                        bookComments.add(comment);
                        booksCommentsByAuthor.put(bookId, bookComments);
                    }
                }
            }
        }

        return booksCommentsByAuthor;
    }

    private Comment makeComment(ResultSet rs) throws SQLException {
        return new Comment(rs.getLong("comment_id"), rs.getString("content"));
    }

    private Book makeBook(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("book_id"), rs.getString("title"),
                rs.getString("description"), rs.getObject("release", LocalDate.class));
    }
}
