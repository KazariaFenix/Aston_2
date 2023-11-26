package repositorytest;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerTest;
import ru.marzuev.db.InitDatabaseImpl;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class AuthorRepositoryTest {
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("litTest")
            .withUsername("login")
            .withPassword("pass");
    AuthorRepository authorRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionManager connectionManager = new ConnectionManagerTest(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        authorRepository = new AuthorRepositoryImpl(connectionManager);
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(InitDatabaseImpl.createTable());
            ps.execute();
        }
    }

    @Test
    void insertAuthor_whenNormal_thenReturnAuthor() throws SQLException {
        Author newAuthor = new Author(0L, "Vlad",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        newAuthor.setId(author.getId());
        assertThat(newAuthor, equalTo(author));
    }

    @Test
    void insertAuthor_whenDuplicateName_throwException() throws SQLException {
        Author newAuthor1 = new Author(0L, "Alex",
                LocalDate.of(1990, 5, 12));
        assertThrows(SQLException.class,
                () -> authorRepository.addAuthor(newAuthor1));
    }

    @Test
    void updateAuthor_whenNormal_thenReturnNewAuthor() throws SQLException {
        Author newAuthor = new Author(0L, "Rom",
                LocalDate.of(1990, 5, 12));
        Author newAuthor1 = new Author(1L, "Roman",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        Author updateAuthor = authorRepository.updateAuthor(newAuthor1);
        assertThat(updateAuthor, equalTo(newAuthor1));
    }

    @Test
    void updateAuthor_whenAuthorNotFound_thenReturnNewAuthor() throws SQLException {
        Author newAuthor = new Author(0L, "Alex",
                LocalDate.of(1990, 5, 12));
        Author newAuthor1 = new Author(0L, "Alexandro",
                LocalDate.of(1990, 5, 12));
        authorRepository.addAuthor(newAuthor);
        assertThrows(SQLException.class,
                () -> authorRepository.updateAuthor(newAuthor1));
    }

    @Test
    void deleteAuthor_whenNormal_throwException() throws SQLException {
        Author newAuthor = new Author(0L, "Serg",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        authorRepository.deleteAuthor(author.getId());
        assertThrows(IllegalArgumentException.class,
                () -> authorRepository.getAuthorById(author.getId()));
    }

    @Test
    void deleteAuthor_whenAuthorNotFound_throwException() throws SQLException {
        Author newAuthor = new Author(0L, "Mack",
                LocalDate.of(1990, 5, 12));
        authorRepository.addAuthor(newAuthor);
        assertThrows(IllegalArgumentException.class,
                () -> authorRepository.deleteAuthor(newAuthor.getId()));
    }

    @Test
    void getAuthorById_whenNormal_thenReturnAuthor() throws SQLException {
        Author newAuthor = new Author(0L, "Rick",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        Author getAuthor = authorRepository.getAuthorById(author.getId());

        assertThat(author, equalTo(getAuthor));
    }

    @Test
    void getAuthorById_whenAuthorNotFound_throwException() throws SQLException {
        Author newAuthor = new Author(0L, "Nick",
                LocalDate.of(1990, 5, 12));
        authorRepository.addAuthor(newAuthor);
        assertThrows(IllegalArgumentException.class,
                () -> authorRepository.getAuthorById(newAuthor.getId()));
    }

    @Test
    void getAuthors_whenNormal_thenReturnAuthors() throws SQLException {
        Author newAuthor = new Author(0L, "Dan",
                LocalDate.of(1990, 5, 12));
        Author newAuthor1 = new Author(0L, "Ivan",
                LocalDate.of(1990, 5, 12));
        authorRepository.addAuthor(newAuthor);
        authorRepository.addAuthor(newAuthor1);
        Map<Author, List<Book>> authors = authorRepository.getAuthorsWithBooks();
        assertThat(authors.size(), equalTo(5));
    }
}
