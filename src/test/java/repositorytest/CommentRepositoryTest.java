package repositorytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.marzuev.db.ConnectionManager;
import ru.marzuev.db.ConnectionManagerTest;
import ru.marzuev.db.InitDatabaseImpl;
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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentRepositoryTest {
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("litTest")
            .withUsername("login")
            .withPassword("pass");
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
    void setUp() throws SQLException {
        ConnectionManager connectionManager = new ConnectionManagerTest(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        bookRepository = new BookRepositoryImpl(connectionManager);
        authorRepository = new AuthorRepositoryImpl(connectionManager);
        commentRepository = new CommentRepositoryImpl(connectionManager);
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(InitDatabaseImpl.createTable());
            ps.execute();
        }
    }

    @Test
    void addComment_whenNormal_thenReturnComment() throws SQLException {
        Author newAuthor = new Author(1L, "Dan",
                LocalDate.of(1990, 5, 12));
        Author author = authorRepository.addAuthor(newAuthor);
        Book book = new Book(0L, "Title", "Description", LocalDate.now());
        Book addBook = bookRepository.addBook(book, List.of(author.getId()));
        Comment comment = new Comment(0L, "Content");
        Comment addComment = commentRepository.addComment(comment, addBook.getId());

        comment.setId(addComment.getId());
        assertThat(addComment, equalTo(comment));
    }

    @Test
    void updateComment_whenNormal_thenReturnComment() throws SQLException {
        Comment comment = new Comment(1L, "ContentNew");
        Comment addComment = commentRepository.addComment(comment, 1L);
        Comment updateComment = commentRepository.updateComment(comment, comment.getId());

        assertThat(comment, equalTo(updateComment));
    }

    @Test
    void updateComment_whenCommentNotFound_throwException() {
        Comment comment = new Comment(1L, "ContentNew");
        assertThrows(SQLException.class, () -> commentRepository.updateComment(comment, 315315L));
    }

    @Test
    void deleteComment_whenNormal_thenReturnComment() throws SQLException {
        Comment comment = new Comment(1L, "ContentNew");
        Comment addComment = commentRepository.addComment(comment, 1L);
        commentRepository.deleteComment(addComment.getId());
    }

    @Test
    void deleteComment_whenCommentNotFound() {
        assertThrows(SQLException.class, () -> commentRepository.deleteComment(255253L));
    }

    @Test
    void getCommentByBookId_whenNormal_thenReturnComments() throws SQLException {
        List<Comment> comments = commentRepository.getCommentsByBookId(1L);

        assertThat(comments.size(), equalTo(0));
    }
}
