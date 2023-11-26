package ru.marzuev.repository;

import ru.marzuev.model.Comment;

import java.sql.SQLException;
import java.util.List;

public interface CommentRepository {

    Comment addComment(Comment comment, long bookId) throws SQLException;

    Comment updateComment(Comment comment, long commentId) throws SQLException;

    void deleteComment(long commentId) throws SQLException;

    Comment getCommentById(long commentId) throws SQLException;

    List<Comment> getCommentsByBookId(long bookId) throws SQLException;
}
