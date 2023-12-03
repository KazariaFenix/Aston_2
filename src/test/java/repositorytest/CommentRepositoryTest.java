package repositorytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.repository.impl.CommentRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentRepositoryTest {
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("litTest")
            .withUsername("login")
            .withPassword("pass")
            .withInitScript("schema.sql");
    BookRepository bookRepository;
    AuthorRepository authorRepository;
    CommentRepository commentRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        ConnectionManager connectionManager = new ConnectionManagerImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        bookRepository = new BookRepositoryImpl(connectionManager);
        authorRepository = new AuthorRepositoryImpl(connectionManager);
        commentRepository = new CommentRepositoryImpl(connectionManager);
    }

    @Test
    void addComment_whenNormal_thenReturnComment() throws SQLException {
        final Author newAuthor = new Author(1L, "Valerian",
                LocalDate.of(1990, 5, 12));
        final Author author = authorRepository.addAuthor(newAuthor);
        final Book book = new Book(0L, "TitleVal", "Description", LocalDate.now());
        final Book addBook = bookRepository.addBook(book, List.of(author.getId()));
        final Comment comment = new Comment(0L, "Content");
        final Comment addComment = commentRepository.addComment(comment, addBook.getId());

        comment.setId(addComment.getId());
        assertThat(addComment, equalTo(comment));
    }

    @Test
    void updateComment_whenNormal_thenReturnComment() throws SQLException {
        final Comment comment = new Comment(1L, "ContentNew");
        final Book book = bookRepository.getBookById(1L);

        comment.setBook(book);
        final Comment addComment = commentRepository.addComment(comment, 1L);
        final Comment updateComment = commentRepository.updateComment(comment, comment.getId());

        assertThat(comment, equalTo(updateComment));
    }

    @Test
    void updateComment_whenCommentNotFound_throwException() {
        final Comment comment = new Comment(1L, "ContentNew");
        assertThrows(SQLException.class, () -> commentRepository.updateComment(comment, 315315L));
    }

    @Test
    void deleteComment_whenNormal_thenReturnComment() throws SQLException {
        final Comment comment = new Comment(1L, "ContentNew");
        final Comment addComment = commentRepository.addComment(comment, 1L);
        commentRepository.deleteComment(addComment.getId());
    }

    @Test
    void deleteComment_whenCommentNotFound() {
        assertThrows(SQLException.class, () -> commentRepository.deleteComment(255253L));
    }

    @Test
    void getCommentByBookId_whenNormal_thenReturnComments() throws SQLException {
        final List<Comment> comments = commentRepository.getCommentsByBookId(1L);

        assertThat(comments.size(), equalTo(1));
    }

    @Test
    void getCommentById_whenNormal_thenReturnComments() throws SQLException {
        final Author newAuthor = new Author(1L, "Dan",
                LocalDate.of(1990, 5, 12));
        final Author author = authorRepository.addAuthor(newAuthor);
        final Book book = new Book(0L, "Title", "Description", LocalDate.now());

        bookRepository.addBook(book, List.of(author.getId()));
        final Comment comment = new Comment(0L, "ContentNew");

        commentRepository.addComment(comment, 1L);
        final Comment findComment = commentRepository.getCommentById(1L);

        assertThat(findComment.getId(), equalTo(1L));
    }

    @Test
    void getCommentById_whenCommentNotFound_throwException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> commentRepository.getCommentById(23523532L));

        assertThat(e.getMessage(), equalTo("Comment Not Found"));
    }

    @Test
    void getCommentsByAuthorId_whenNormal_thenReturnComments() throws SQLException {
        final long authorId = 1L;
        final Map<Long, List<Comment>> authorComments = commentRepository.getCommentByBookByAuthorId(authorId);

        assertThat(authorComments.size(), equalTo(1));
    }
}
