package ru.marzuev.service.impl;

import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.model.mapper.BookMapper;
import ru.marzuev.model.mapper.CommentMapper;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.repository.impl.CommentRepositoryImpl;
import ru.marzuev.service.BookService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {
    BookRepository bookRepository;
    AuthorRepository authorRepository;
    CommentRepository commentRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
                           CommentRepository commentRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public BookDto addBook(BookDto bookDto, List<Long> authorsList) throws SQLException {
        for (Long authorId : authorsList) {
            authorRepository.getAuthorById(authorId);
        }
        Book book = bookRepository.addBook(BookMapper.toBook(bookDto, 0), authorsList);

        return BookMapper.toBookDto(book, authorsList, new ArrayList<>());
    }

    @Override
    public BookDto updateBook(BookDto bookDto, long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        Book updateBook = bookRepository.updateBook(BookMapper.toBook(bookDto, bookId));
        List<Long> authors = bookRepository.findAuthorByBookId(bookId);
        List<CommentDto> comments = commentRepository.getCommentsByBookId(bookId).stream()
                .map(comment -> CommentMapper.toCommentDto(comment))
                .collect(Collectors.toList());

        return BookMapper.toBookDto(updateBook, authors, comments);
    }

    @Override
    public void deleteBook(long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        bookRepository.deleteBook(bookId);
    }

    @Override
    public BookDto getBookById(long bookId) throws SQLException {
        Book book = bookRepository.getBookById(bookId);
        List<Long> authors = bookRepository.findAuthorByBookId(bookId);
        List<CommentDto> comments = commentRepository.getCommentsByBookId(book.getId()).stream()
                .map(comment -> CommentMapper.toCommentDto(comment))
                .collect(Collectors.toList());
        return BookMapper.toBookDto(book, authors, comments);
    }

    @Override
    public List<BookDto> getBooksByAuthorId(long authorId) throws SQLException {
        List<Book> books = bookRepository.getBooksByAuthorId(authorId);
        List<Comment> bookComments = new ArrayList<>();
        for (Book book : books) {
            bookComments.addAll(commentRepository.getCommentsByBookId(book.getId()));
        }

        List<CommentDto> bookCommentsDto = bookComments.stream()
                .map(comment -> CommentMapper.toCommentDto(comment))
                .collect(Collectors.toList());

        return books.stream()
                .map(book -> BookMapper.toBookDto(book, new ArrayList<>(List.of(authorId)), bookCommentsDto))
                .collect(Collectors.toList());
    }
}
