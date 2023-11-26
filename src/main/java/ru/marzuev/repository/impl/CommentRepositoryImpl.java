package ru.marzuev.repository.impl;

import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.Comment;
import ru.marzuev.repository.CommentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepositoryImpl implements CommentRepository {
    ConnectionManager connectionManager;


    public CommentRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Comment addComment(Comment comment, long bookId) throws SQLException {
        Connection connection = connectionManager.getConnection();
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

        PreparedStatement ps1 = connection.prepareStatement("INSERT INTO books_comments (book_id, comment_id) " +
                "VALUES ( ?, ?)");

        ps1.setLong(1, bookId);
        ps1.setLong(2, commentId);
        ps1.executeUpdate();
        ps1.close();

        PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");
        ps2.setLong(1, commentId);

        Comment postComment = null;
        try (ResultSet rs = ps2.executeQuery()) {
            while (rs.next()) {
                postComment = makeComment(rs);
            }
        }
        connection.close();

        return postComment;
    }

    @Override
    public Comment updateComment(Comment comment, long commentId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE comments SET content = ? WHERE comment_id = ?");

        ps.setString(1, comment.getContent());
        ps.setLong(2, commentId);
        int count = ps.executeUpdate();

        if (count == 0) {
            throw new SQLException("Comment Is Not Update");
        }
        Comment updateComment = null;
        PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");
        ps2.setLong(1, commentId);

        try (ResultSet rs = ps2.executeQuery()) {
            while (rs.next()) {
                updateComment = makeComment(rs);
            }
        }
        connection.close();

        return updateComment;
    }

    @Override
    public void deleteComment(long commentId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement ps = connection.prepareStatement("DELETE FROM comments WHERE comment_id = ?");

        ps.setLong(1, commentId);
        int count = ps.executeUpdate();

        if (count == 0) {
            throw new SQLException("Comment Is Not Update");
        }
        connection.close();
    }

    @Override
    public Comment getCommentById(long commentId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments WHERE comment_id = ?");

        statement.setLong(1, commentId);
        Comment findComment = null;
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                findComment = makeComment(results);
            }
        }
        connection.close();
        if (findComment == null) {
            throw new IllegalArgumentException();
        } else {
            return findComment;
        }
    }

    @Override
    public List<Comment> getCommentsByBookId(long bookId) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments WHERE comment_id IN " +
                "(SELECT comment_id FROM books_comments WHERE book_id = ?)");

        statement.setLong(1, bookId);
        List<Comment> bookComments = new ArrayList<>();
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                bookComments.add(makeComment(results));
            }
        }
        connection.close();

        return bookComments;
    }

    private Comment makeComment(ResultSet rs) throws SQLException {
        return new Comment(rs.getLong("comment_id"), rs.getString("content"));
    }
}
