package testservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.repository.AuthorRepository;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.service.impl.BookServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    BookServiceImpl bookService;
    @Mock
    BookRepository bookRepository;
    @Mock
    AuthorRepository authorRepository;
    @Mock
    CommentRepository commentRepository;

    @Test
    void addBook_whenNormal_thenReturnBook() throws SQLException {
        final Author author = new Author(1L, "Bob", LocalDate.of(1990, 10, 28));
        final Book book = new Book(0, "Book", "Description", LocalDate.now());
        final BookDto bookDto = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
        final BookDto bookDtoExpected = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(List.of(1L)), new ArrayList<>());
        Mockito
                .when(bookRepository.addBook(book, List.of(1L)))
                .thenReturn(book);
        Mockito
                .when(authorRepository.getAuthorById(1L))
                .thenReturn(author);

        BookDto saveBook = bookService.addBook(bookDto, List.of(1L));
        assertThat(bookDtoExpected, equalTo(saveBook));
        Mockito.verify(authorRepository, Mockito.times(1))
                .getAuthorById(1L);
    }

    @Test
    void addBook_whenAuthorNotFound_thenThrowException() throws SQLException {
        final Author author = new Author(1L, "Bob", LocalDate.of(1990, 10, 28));
        final BookDto bookDto = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
        Mockito
                .when(authorRepository.getAuthorById(author.getId()))
                .thenThrow(new IllegalArgumentException("Author Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(bookDto, List.of(author.getId())));

        assertThat(e.getMessage(), equalTo("Author Not Found"));
        Mockito.verify(authorRepository, Mockito.times(1))
                .getAuthorById(author.getId());
    }

    @Test
    void updateBook_whenNormal_thenReturnUpdateBook() throws SQLException {
        final Author author = new Author(1L, "Bob", LocalDate.of(1990, 10, 28));
        final Book book = new Book(1L, "Book", "Description", LocalDate.now());
        final BookDto bookDto = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
        final BookDto bookDtoExpected = new BookDto("Book", "Description", LocalDate.now(),
                List.of(1L), new ArrayList<>());

        bookDto.setAuthors(List.of(1L));
        Mockito
                .doReturn(book)
                .when(bookRepository).updateBook(book);
        Mockito
                .doReturn(List.of(author))
                .when(bookRepository).findAuthorByBookId(1L);
        Mockito
                .doReturn(new ArrayList<>())
                .when(commentRepository).getCommentsByBookId(1L);

        BookDto updateBook = bookService.updateBook(bookDtoExpected, book.getId());
        assertThat(updateBook, equalTo(bookDto));
        Mockito.verify(bookRepository, Mockito.times(1)).findAuthorByBookId(book.getId());
        Mockito.verify(commentRepository, Mockito.times(1)).getCommentsByBookId(book.getId());
    }

    @Test
    void updateBook_whenBookNotFound_thenThrowException() throws SQLException {
        final Book book = new Book(1, "Book", "Description", LocalDate.now());
        final BookDto bookDto = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
        Mockito
                .when(bookRepository.getBookById(book.getId()))
                .thenThrow(new IllegalArgumentException("Book Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(bookDto, book.getId()));

        assertThat(e.getMessage(), equalTo("Book Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBookById(book.getId());
    }

    @Test
    void deleteBook_whenNormal_thenDeleteBookk() throws SQLException {
        final long bookId = 1L;
        Mockito
                .doNothing()
                .when(bookRepository).deleteBook(bookId);

        bookService.deleteBook(bookId);
        Mockito.verify(bookRepository, Mockito.times(1))
                .deleteBook(bookId);
    }

    @Test
    void deleteBook_whenBookNotFound_thenDeleteBook() throws SQLException {
        final long bookId = 1L;
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenThrow(new IllegalArgumentException("Book Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook(bookId));
        assertThat(e.getMessage(), equalTo("Book Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBookById(bookId);
    }

    @Test
    void getBookById_whenNormal_thenReturnBook() throws SQLException {
        final long bookId = 1L;
        final Book book = new Book(1, "Book", "Description", LocalDate.now());
        final BookDto bookDto = new BookDto("Book", "Description", LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenReturn(book);

        BookDto findBook = bookService.getBookById(bookId);
        assertThat(bookDto, equalTo(findBook));
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBookById(bookId);
    }

    @Test
    void getBookById_whenNormalWithComment_thenReturnBook() throws SQLException {
        final long bookId = 1L;
        final Author author = new Author(1L, "M", LocalDate.of(1890, 10, 21));
        final Book book = new Book(1, "Book", "Description", LocalDate.now());
        final Comment comment = new Comment(1, "Comment");

        book.setListAuthors(List.of(author));
        comment.setBook(book);
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenReturn(book);
        Mockito
                .when(bookRepository.findAuthorByBookId(bookId))
                .thenReturn(List.of(author));
        Mockito
                .when(commentRepository.getCommentsByBookId(bookId))
                .thenReturn(List.of(comment));

        BookDto findBook = bookService.getBookById(bookId);
        assertThat(findBook.getComments().size(), equalTo(1));
        Mockito.verify(commentRepository, Mockito.times(1))
                .getCommentsByBookId(bookId);
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBookById(bookId);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAuthorByBookId(bookId);
    }

    @Test
    void getBookById_whenBookNotFound_thenThrowException() throws SQLException {
        final long bookId = 1L;
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenThrow(new IllegalArgumentException("Book Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(bookId));
        assertThat(e.getMessage(), equalTo("Book Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBookById(bookId);
    }

    @Test
    void getBooksByAuthorId_whenNormal_thenReturnBook() throws SQLException {
        final long authorId = 1;
        final Book book = new Book(1, "Book", "Description", LocalDate.now());
        final Book book1 = new Book(2, "Book1", "Description", LocalDate.now());
        Mockito
                .when(bookRepository.getBooksByAuthorId(authorId))
                .thenReturn(List.of(book, book1));
        Mockito
                .when(authorRepository.getAuthorById(authorId))
                .thenReturn(new Author(1L, "Rayan", LocalDate.of(1994, 12,25)));

        Mockito
                .when(commentRepository.getCommentByBookByAuthorId(authorId))
                .thenReturn(new HashMap<>());

        List<BookDto> booksDtoByAuthorId = bookService.getBooksByAuthorId(authorId);
        assertThat(booksDtoByAuthorId.size(), equalTo(2));
        Mockito.verify(bookRepository, Mockito.times(1)).getBooksByAuthorId(authorId);
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorById(authorId);
        Mockito.verify(commentRepository, Mockito.times(1)).getCommentByBookByAuthorId(authorId);
    }

    @Test
    void getBooksByAuthorId_whenAuthorNotFound_thenReturnBook() throws SQLException {
        final long authorId = 1;
        Mockito
                .when(bookRepository.getBooksByAuthorId(authorId))
                .thenThrow(new IllegalArgumentException("Author Not Found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBooksByAuthorId(authorId));
        assertThat(e.getMessage(), equalTo("Author Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1))
                .getBooksByAuthorId(authorId);
    }
}
