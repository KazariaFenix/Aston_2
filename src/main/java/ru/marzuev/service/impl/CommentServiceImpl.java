package ru.marzuev.service.impl;

import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.model.mapper.CommentMapper;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.repository.impl.CommentRepositoryImpl;
import ru.marzuev.service.CommentService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    BookRepository bookRepository;

    public CommentServiceImpl(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        Comment comment = CommentMapper.toComment(commentDto, 0);
        return CommentMapper.toCommentDto(commentRepository.addComment(comment, bookId));
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, long commentId) throws SQLException {
        commentRepository.getCommentById(commentId);
        Comment comment = CommentMapper.toComment(commentDto, commentId);
        return CommentMapper.toCommentDto(commentRepository.updateComment(comment, commentId));
    }

    @Override
    public void deleteComment(long commentId) throws SQLException {
        commentRepository.getCommentById(commentId);
        commentRepository.deleteComment(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByBookId(long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        List<Comment> bookComments = commentRepository.getCommentsByBookId(bookId);

        return bookComments.stream()
                .map(comment -> CommentMapper.toCommentDto(comment))
                .collect(Collectors.toList());
    }
}
