package ru.marzuev.service;

import ru.marzuev.model.dto.CommentDto;

import java.sql.SQLException;
import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto commentDto, long bookId) throws SQLException;

    CommentDto updateComment(CommentDto commentDto, long commentId) throws SQLException;

    void deleteComment(long commentId) throws SQLException;

    List<CommentDto> getCommentsByBookId(long bookId) throws SQLException;

}
