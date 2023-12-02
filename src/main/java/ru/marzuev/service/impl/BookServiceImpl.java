package ru.marzuev.service.impl;

import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.model.mapper.BookMapper;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.service.BookService;

import java.sql.SQLException;
import java.util.*;
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
        List<Author> authors = new ArrayList<>();
        for (Long authorId : authorsList) {
            authors.add(authorRepository.getAuthorById(authorId));
        }
        Book book = bookRepository.addBook(BookMapper.toBook(bookDto, 0), authorsList);
        book.setListAuthors(authors);
        book.setListComments(new ArrayList<>());

        return BookMapper.toBookDto(book);
    }

    @Override
    public BookDto updateBook(BookDto bookDto, long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        Book updateBook = bookRepository.updateBook(BookMapper.toBook(bookDto, bookId));
        List<Author> authors = bookRepository.findAuthorByBookId(bookId);
        List<Comment> comments = commentRepository.getCommentsByBookId(bookId);
        comments.stream()
                .peek(comment -> comment.setBook(updateBook))
                .collect(Collectors.toList());
        updateBook.setListAuthors(authors);
        updateBook.setListComments(comments);

        return BookMapper.toBookDto(updateBook);
    }

    @Override
    public void deleteBook(long bookId) throws SQLException {
        bookRepository.getBookById(bookId);
        bookRepository.deleteBook(bookId);
    }

    @Override
    public BookDto getBookById(long bookId) throws SQLException {
        Book book = bookRepository.getBookById(bookId);
        List<Author> authors = bookRepository.findAuthorByBookId(bookId);
        List<Comment> comments = commentRepository.getCommentsByBookId(book.getId());
        comments.stream()
                .peek(comment -> comment.setBook(book))
                .collect(Collectors.toList());
        book.setListComments(comments);
        book.setListAuthors(authors);

        return BookMapper.toBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByAuthorId(long authorId) throws SQLException {
        List<Book> books = bookRepository.getBooksByAuthorId(authorId);
        Author author = authorRepository.getAuthorById(authorId);
        Map<Long, List<Comment>> booksComments = commentRepository.getCommentByBookByAuthorId(authorId);
        books = books.stream()
                .peek(book -> book.setListAuthors(List.of(author)))
                .collect(Collectors.toList());

        for (Book book : books) {
            List<Comment> bookComments = new ArrayList<>();
            for (Map.Entry<Long, List<Comment>> longListEntry : booksComments.entrySet()) {
                if (longListEntry.getKey() == book.getId()) {
                    bookComments.addAll(longListEntry.getValue());
                }
            }
            book.setListComments(bookComments);
            bookComments.stream()
                    .peek(comment -> comment.setBook(book))
                    .collect(Collectors.toList());
        }
        return books.stream()
                .map(book -> BookMapper.toBookDto(book))
                .collect(Collectors.toList());
    }
}
