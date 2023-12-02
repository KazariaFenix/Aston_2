package ru.marzuev.service.impl;

import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.dto.AuthorDto;
import ru.marzuev.model.mapper.AuthorMapper;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.service.AuthorService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthorServiceImpl implements AuthorService {

    AuthorRepository repository;
    BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    @Override
    public AuthorDto addAuthor(AuthorDto authorDto) throws SQLException {
        Author author = AuthorMapper.toAuthor(0, authorDto);

        return AuthorMapper.toAuthorDto(repository.addAuthor(author));
    }

    @Override
    public AuthorDto updateAuthor(AuthorDto authorDto, long authorId) throws SQLException {
        repository.getAuthorById(authorId);
        Author author = repository.updateAuthor(AuthorMapper.toAuthor(authorId, authorDto));

        return AuthorMapper.toAuthorDto(author);
    }

    @Override
    public void deleteAuthor(long authorId) throws SQLException {
        repository.getAuthorById(authorId);
        repository.deleteAuthor(authorId);
    }

    @Override
    public AuthorDto getAuthorById(long authorId) throws SQLException {
        Author author = repository.getAuthorById(authorId);

        return AuthorMapper.toAuthorDto(author);
    }

    @Override
    public List<AuthorDto> getAuthors() throws SQLException {
        Map<Author, List<Book>> authorWiyhBooks = repository.getAuthorsWithBooks();
        return authorWiyhBooks.entrySet().stream()
                .peek(entry -> entry.getKey().setListBooks(entry.getValue()))
                .map(entry -> AuthorMapper.toAuthorDto(entry.getKey()))
                .collect(Collectors.toList());
    }
}
