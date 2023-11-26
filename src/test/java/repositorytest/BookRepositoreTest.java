package repositorytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerTest;
import ru.marzuev.db.InitDatabaseImpl;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;
import ru.marzuev.repository.impl.BookRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class BookRepositoreTest {
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("litTest")
            .withUsername("login")
            .withPassword("pass");
    BookRepository bookRepository;
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
        bookRepository = new BookRepositoryImpl(connectionManager);
        authorRepository = new AuthorRepositoryImpl(connectionManager);
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(InitDatabaseImpl.createTable());
            ps.execute();
        }
    }

    @Test
    void insertBook_whenNormal_thenReturnBook() throws SQLException {
        Author newAuthor = new Author(1L, "Dan",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        Book book = new Book(0L, "Title", "Description", LocalDate.now());
        Book addBook = bookRepository.addBook(book, List.of(author.getId()));

        book.setId(addBook.getId());
        assertThat(book, equalTo(addBook));
    }

    @Test
    void insertBook_whenTitleDuplicate_throwException() {
        Book book = new Book(0L, "Title", "Description", LocalDate.now());
        assertThrows(SQLException.class,
                () -> bookRepository.addBook(book, List.of(3532532L)));
    }

    @Test
    void updateBook_whenNormal_thenReturnUpdateBook() throws SQLException {
        Book book = new Book(1L, "TitleName", "Description", LocalDate.now());
        Book updateBook = bookRepository.updateBook(book);

        assertThat(book, equalTo(updateBook));
    }

    @Test
    void updatetBook_whenTitleDuplicate_throwException() {
        Book book = new Book(0L, "Title", "Description", LocalDate.now());
        assertThrows(SQLException.class,
                () -> bookRepository.updateBook(book));
    }

    @Test
    void deleteBook_whenNormal_thenDeleteBook() throws SQLException {
        Book book1 = new Book(0L, "TitleSecond", "Description", LocalDate.now());
        Book book3 = bookRepository.addBook(book1, List.of(1L));
        bookRepository.deleteBook(book3.getId());
        assertThrows(IllegalArgumentException.class, () -> bookRepository.getBookById(book3.getId()));
    }

    @Test
    void deleteBook_whenBookNotFound_throwException() {
        assertThrows(SQLException.class, () -> bookRepository.deleteBook(114214L));
    }

    @Test
    void getBookById_whenNormal_thenReturnBook() throws SQLException {
        Book book = bookRepository.addBook(new Book(0L, "TS", "Description", LocalDate.now()),
                List.of(1L));
        Book getBook = bookRepository.getBookById(book.getId());

        assertThat(book, equalTo(getBook));
    }

    @Test
    void getBookById_whenBookNotFound_throwException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookRepository.getBookById(262363225L));
    }

    @Test
    void getBooksByAythorId_whenNormal_thenReturnBooks() throws SQLException {
        bookRepository.addBook(new Book(0L, "Java", "Description", LocalDate.now()),
                List.of(1L));
        bookRepository.addBook(new Book(0L, "Python", "Description", LocalDate.now()),
                List.of(1L));
        List<Book> books = bookRepository.getBooksByAuthorId(1L);

        assertThat(books.size(), equalTo(3));
    }
}
