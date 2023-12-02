package testservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.dto.AuthorDto;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.service.impl.AuthorServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @InjectMocks
    AuthorServiceImpl authorService;
    @Mock
    AuthorRepository authorRepository;
    @Mock
    BookRepository bookRepository;

    @Test
    void addAuthor_whenNormal_thenReturnAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("Bob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author author = new Author(0L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.addAuthor(author))
                .thenReturn(author);

        AuthorDto savedAuthor = authorService.addAuthor(authorDto);

        assertThat(authorDto, equalTo(savedAuthor));
        Mockito.verify(authorRepository, Mockito.times(1)).addAuthor(any());
    }

    @Test
    void addUser_whenInvalidName_thenReturnSqlException() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("Bob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author author = new Author(0L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.addAuthor(author))
                .thenThrow(new SQLException("Author Is Not Insert"));

        SQLException e = assertThrows(SQLException.class,
                () -> authorService.addAuthor(authorDto));

        assertThat(e.getMessage(), equalTo("Author Is Not Insert"));
        Mockito.verify(authorRepository, Mockito.times(1)).addAuthor(any());
    }

    @Test
    void updateAuthor_whenNormal_thenReturnUpdateAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("UpdateBob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author newAuthor = new Author(1L, "UpdateBob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.updateAuthor(newAuthor))
                .thenReturn(newAuthor);

        AuthorDto updateAuthor = authorService.updateAuthor(authorDto, 1);
        assertThat(authorDto, equalTo(updateAuthor));
        Mockito.verify(authorRepository, Mockito.times(1)).updateAuthor(any());
    }

    @Test
    void updateAuthor_whenNotFoundAuthor_thenReturnUpdateAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("UpdateBob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author author = new Author(1L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.getAuthorById(author.getId()))
                .thenThrow(new IllegalArgumentException("Author Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> authorService.updateAuthor(authorDto, 1));
        assertThat(e.getMessage(), equalTo("Author Not Found"));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(1L);
    }

    @Test
    void updateAuthor_whenInvalidName_thenReturnUpdateAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("UpdateBob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author newAuthor = new Author(1L, "UpdateBob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.updateAuthor(newAuthor))
                .thenThrow(new SQLException("Author Is Not Insert"));

        SQLException e = assertThrows(SQLException.class,
                () -> authorService.updateAuthor(authorDto, 1));
        assertThat(e.getMessage(), equalTo("Author Is Not Insert"));
        Mockito.verify(authorRepository, Mockito.times(1)).updateAuthor(newAuthor);
    }

    @Test
    void deleteAuthor_whenNormal_thenDeleteAuthor() throws SQLException {
        final Author newAuthor = new Author(1L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .doNothing()
                .when(authorRepository).deleteAuthor(newAuthor.getId());
        authorService.deleteAuthor(newAuthor.getId());
        Mockito.verify(authorRepository, Mockito.times(1)).deleteAuthor(newAuthor.getId());
    }

    @Test
    void deleteAuthor_whenNotFoundAuthor_thenDeleteAuthor() throws SQLException {
        final Author newAuthor = new Author(1L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .doThrow(new SQLException("Author Not Found"))
                .when(authorRepository).getAuthorById(newAuthor.getId());
        SQLException e  = assertThrows(SQLException.class,
                () -> authorService.deleteAuthor(newAuthor.getId()));

        assertThat(e.getMessage(), equalTo("Author Not Found"));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(newAuthor.getId());
    }

    @Test
    void getAuthorById_whenNormal_thenReturnAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("Bob", LocalDate.of(1990, 10,28),
                new ArrayList<>());
        final Author newAuthor = new Author(1L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.getAuthorById(newAuthor.getId()))
                .thenReturn(newAuthor);

        AuthorDto author = authorService.getAuthorById(newAuthor.getId());
        assertThat(author, equalTo(authorDto));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(newAuthor.getId());
    }

    @Test
    void getAuthorById_whenNormalWithBooks_thenReturnAuthor() throws SQLException {
        final AuthorDto authorDto = new AuthorDto("Bob", LocalDate.of(1990, 10,28),
                List.of("Book0", "Book1"));
        final Author newAuthor = new Author(1L, "Bob", LocalDate.of(1990, 10,28));
        final Book book0 = new Book(1L, "Book0", "Description0", LocalDate.now());
        final Book book1 = new Book(2l, "Book1", "Description1", LocalDate.now());
        newAuthor.setListBooks(List.of(book0, book1));

        Mockito
                .when(authorRepository.getAuthorById(newAuthor.getId()))
                .thenReturn(newAuthor);

        AuthorDto author = authorService.getAuthorById(newAuthor.getId());
        assertThat(author, equalTo(authorDto));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(newAuthor.getId());
    }

    @Test
    void getAuthorById_whenNotFoundAuthor_thenReturnAuthor() throws SQLException {
        final Author newAuthor = new Author(1L, "Bob", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.getAuthorById(newAuthor.getId()))
                .thenThrow(new SQLException("Author Not Found"));

        SQLException e  = assertThrows(SQLException.class,
                () -> authorService.deleteAuthor(newAuthor.getId()));

        assertThat(e.getMessage(), equalTo("Author Not Found"));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(newAuthor.getId());
    }

    @Test
    void getAuthors_whenNormal_thenReturnAuthor() throws SQLException {
        final Author newAuthor1 = new Author(1L, "Bob", LocalDate.of(1990, 10,28));
        final Author newAuthor = new Author(2L, "Alex", LocalDate.of(1990, 10,28));

        Mockito
                .when(authorRepository.getAuthorsWithBooks())
                .thenReturn(Map.of(newAuthor, new ArrayList<>(), newAuthor1, new ArrayList<>()));

        List<AuthorDto> listAuthors = authorService.getAuthors();

        assertThat(listAuthors.size(), equalTo(2));
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsWithBooks();
    }
}
