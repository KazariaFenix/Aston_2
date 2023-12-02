package ru.marzuev.service.impl;

import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.model.mapper.CommentMapper;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
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
        Book book = bookRepository.getBookById(bookId);
        Comment comment = CommentMapper.toComment(commentDto, 0);
        Comment saveComment = commentRepository.addComment(comment, bookId);
        saveComment.setBook(book);

        return CommentMapper.toCommentDto(saveComment);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, long commentId) throws SQLException {
        Comment oldComment = commentRepository.getCommentById(commentId);
        Comment comment = CommentMapper.toComment(commentDto, commentId);
        comment.setBook(oldComment.getBook());

        return CommentMapper.toCommentDto(commentRepository.updateComment(comment, commentId));
    }

    @Override
    public void deleteComment(long commentId) throws SQLException {
        commentRepository.getCommentById(commentId);
        commentRepository.deleteComment(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByBookId(long bookId) throws SQLException {
        Book book = bookRepository.getBookById(bookId);
        List<Comment> bookComments = commentRepository.getCommentsByBookId(bookId);

        return bookComments.stream()
                .peek(comment -> comment.setBook(book))
                .map(comment -> CommentMapper.toCommentDto(comment))
                .collect(Collectors.toList());
    }
}
