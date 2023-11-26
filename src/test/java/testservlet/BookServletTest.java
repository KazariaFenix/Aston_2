package testservlet;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.service.BookService;
import ru.marzuev.servlet.BookServlet;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookServletTest {
    Gson gson = new Gson();
    @InjectMocks
    BookServlet bookServlet;
    @Mock
    BookService bookService;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnBookDto() throws IOException, SQLException {
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), new ArrayList<>(),
                new ArrayList<>());
        final BookDto bookDto1 = new BookDto("Title", "Description", LocalDate.of(1999, 10,5),
                new ArrayList<>(List.of(1L)), new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);
        final List<Long> authorList = new ArrayList<>();
        authorList.add(1L);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(bookDto1)
                    .when(bookService).addBook(bookDto, authorList);
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("author");
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            bookServlet.doPost(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).addBook(bookDto, authorList);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenAuthorNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(bookService).addBook(bookDto, List.of(1L));
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("author");

            Mockito
                    .doNothing()
                    .when(response).sendError(409, "Author Not Found");

            bookServlet.doPost(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).addBook(bookDto, List.of(1L));
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenRepeatTitle_throwSQLException() throws IOException, SQLException {
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(bookService).addBook(bookDto, List.of(1L));
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("author");

            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");
            bookServlet.doPost(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).addBook(bookDto, List.of(1L));
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void updateBook_whenNormal_thenReturnAuthor() throws IOException, SQLException {
        final long bookId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/books/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/books")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bookDto)
                    .when(bookService).updateBook(bookDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            bookServlet.doPut(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).updateBook(bookDto, bookId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateBook_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long bookId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/books")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/books")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doNothing()
                    .when(response).sendError(406, "Book Id Invalid");

            bookServlet.doPut(request, response);
        }
        Mockito.verify(bookService, Mockito.never()).updateBook(bookDto, bookId);
        Mockito.verify(response, Mockito.times(1))
                .sendError(406, "Book Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
    }

    @Test
    void updateBook_whenBookNotFound_throwException() throws IOException, SQLException {
        final long bookId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/books/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/books")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Book Not Found"))
                    .when(bookService).updateBook(bookDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Book Not Found");

            bookServlet.doPut(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).updateBook(bookDto, bookId);
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateBook_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long bookId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final String responseBody = gson.toJson(bookDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/books/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/books")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(bookService).updateBook(bookDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            bookServlet.doPut(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).updateBook(bookDto, bookId);
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteBook_whenNormal_thenDeleteBook() throws SQLException, IOException {
        final long bookId = 1;
        Mockito
                .doNothing()
                .when(bookService).deleteBook(bookId);
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        bookServlet.doDelete(request, response);
        Mockito.verify(bookService, Mockito.times(1)).deleteBook(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteBook_whenAuthorNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long bookId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(bookService).deleteBook(bookId);
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(404, "Book Not Found");
        bookServlet.doDelete(request, response);
        Mockito.verify(bookService, Mockito.times(1)).deleteBook(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(404, "Book Not Found");
    }

    @Test
    void deleteBook_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long bookId = 1;
        Mockito
                .doThrow(new SQLException())
                .when(bookService).deleteBook(bookId);
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        bookServlet.doDelete(request, response);
        Mockito.verify(bookService, Mockito.times(1)).deleteBook(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(500, "Server Error");
    }

    @Test
    void deleteBook_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing()
                .when(response).sendError(406, "Book Id Invalid");
        Mockito
                .doReturn("/books")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        bookServlet.doDelete(request, response);
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(406, "Book Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getServletPath();
    }

    @Test
    void getBookById_whenNormal_thenReturnBookDto() throws IOException, SQLException {
        final long bookId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        String responseBody = gson.toJson(bookDto);
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doReturn(bookDto)
                .when(bookService).getBookById(bookId);
        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            bookServlet.doGet(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).getBookById(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).getWriter();
    }

    @Test
    void getBooksByAuthorId_whenNormal_thenReturnBooks() throws IOException, SQLException {
        final long authorId = 1;
        final BookDto bookDto = new BookDto("Title", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        final BookDto bookDto1 = new BookDto("Title1", "Description", LocalDate.now(), List.of(1L),
                new ArrayList<>());
        String responseBody = gson.toJson(bookDto);
        Mockito
                .doReturn("/books")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doReturn("1")
                .when(request).getParameter("author");
        Mockito
                .doReturn(List.of(bookDto, bookDto1))
                .when(bookService).getBooksByAuthorId(authorId);
        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            bookServlet.doGet(request, response);
        }
        Mockito.verify(bookService, Mockito.times(1)).getBooksByAuthorId(authorId);
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).getWriter();
    }

    @Test
    void getBookById_whenBookNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final long bookId = 1;
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(bookService).getBookById(bookId);
        Mockito
                .doNothing()
                .when(response).sendError(404, "Book Not Found");
        bookServlet.doGet(request, response);
        Mockito.verify(bookService, Mockito.times(1)).getBookById(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).sendError(404, "Book Not Found");
    }

    @Test
    void getBookById_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long bookId = 1;
        Mockito
                .doReturn("/books/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/books")
                .when(request).getServletPath();
        Mockito
                .doThrow(new SQLException())
                .when(bookService).getBookById(bookId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        bookServlet.doGet(request, response);
        Mockito.verify(bookService, Mockito.times(1)).getBookById(bookId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).sendError(500, "Server Error");
    }
}

