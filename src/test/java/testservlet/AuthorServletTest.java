package testservlet;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.marzuev.model.dto.AuthorDto;
import ru.marzuev.service.AuthorService;
import ru.marzuev.servlet.AuthorServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthorServletTest {
    private Gson gson = new Gson();
    @InjectMocks
    private AuthorServlet authorServlet;
    @Mock
    private AuthorService authorService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnAuthorDto() throws IOException, SQLException {
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(authorDto)
                    .when(authorService).addAuthor(authorDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            authorServlet.doPost(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).addAuthor(authorDto);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenRepeatName_throwIllegalArgumentException() throws IOException, SQLException {
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(authorService).addAuthor(authorDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doNothing()
                    .when(response).sendError(409, "Author Already Exists");
            authorServlet.doPost(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).addAuthor(authorDto);
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenRepeatName_throwSQLException() throws IOException, SQLException {
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(authorService).addAuthor(authorDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");
            authorServlet.doPost(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).addAuthor(authorDto);
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void updateAuthor_whenNormal_thenReturnAuthor() throws IOException, SQLException {
        final long authorId = 1;
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/authors/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/authors")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(authorDto)
                    .when(authorService).updateAuthor(authorDto, authorId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            authorServlet.doPut(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).updateAuthor(authorDto, authorId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateAuthor_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long authorId = 1;
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/authors")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/authors")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doNothing()
                    .when(response).sendError(406, "Author Id Invalid");

            authorServlet.doPut(request, response);
        }
        Mockito.verify(authorService, Mockito.never()).updateAuthor(authorDto, authorId);
        Mockito.verify(response, Mockito.times(1))
                .sendError(406, "Author Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
    }

    @Test
    void updateAuthor_whenAuthorNotFound_throwException() throws IOException, SQLException {
        final long authorId = 1;
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/authors/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/authors")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Author Not Found"))
                    .when(authorService).updateAuthor(authorDto, authorId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Author Not Found");

            authorServlet.doPut(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).updateAuthor(authorDto, authorId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateAuthor_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long authorId = 1;
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final String responseBody = gson.toJson(authorDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/authors/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/authors")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(authorService).updateAuthor(authorDto, authorId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            authorServlet.doPut(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).updateAuthor(authorDto, authorId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteAuthor_whenNormal_thenDeleteAuthor() throws SQLException, IOException {
        final long authorId = 1;
        Mockito
                .doNothing()
                .when(authorService).deleteAuthor(authorId);
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        authorServlet.doDelete(request, response);
        Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteAuthor_whenAuthorNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long authorId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(authorService).deleteAuthor(authorId);
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(404, "Author Not Found");
        authorServlet.doDelete(request, response);
        Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(404, "Author Not Found");
    }

    @Test
    void deleteAuthor_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long authorId = 1;
        Mockito
                .doThrow(new SQLException())
                .when(authorService).deleteAuthor(authorId);
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        authorServlet.doDelete(request, response);
        Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(500, "Server Error");
    }

    @Test
    void deleteAuthor_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing()
                .when(response).sendError(406, "Author Id Invalid");
        Mockito
                .doReturn("/authors")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        authorServlet.doDelete(request, response);
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(406, "Author Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getServletPath();
    }

    @Test
    void getAuthorById_whenNormal_thenReturnAuthorDto() throws IOException, SQLException {
        final long authorId = 1;
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        String responseBody = gson.toJson(authorDto);
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doReturn(authorDto)
                .when(authorService).getAuthorById(authorId);
        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            authorServlet.doGet(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).getAuthorById(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).getWriter();
    }

    @Test
    void getAuthors_whenNormal_thenReturnAuthorDto() throws IOException, SQLException {
        final AuthorDto authorDto = new AuthorDto("Vladimir",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        final AuthorDto authorDto1 = new AuthorDto("Alex",
                LocalDate.of(1960, 10, 21), new ArrayList<>());
        String responseBody = gson.toJson(authorDto);
        Mockito
                .doReturn("/authors")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doReturn(List.of(authorDto, authorDto1))
                .when(authorService).getAuthors();
        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            authorServlet.doGet(request, response);
        }
        Mockito.verify(authorService, Mockito.times(1)).getAuthors();
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).getWriter();
    }

    @Test
    void getAuthorById_whenAuthorNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final long authorId = 1;
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(authorService).getAuthorById(authorId);
        Mockito
                .doNothing()
                .when(response).sendError(404, "Author Not Found");
        authorServlet.doGet(request, response);
        Mockito.verify(authorService, Mockito.times(1)).getAuthorById(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).sendError(404, "Author Not Found");
    }

    @Test
    void getAuthorById_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long authorId = 1;
        Mockito
                .doReturn("/authors/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/authors")
                .when(request).getServletPath();
        Mockito
                .doThrow(new SQLException())
                .when(authorService).getAuthorById(authorId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        authorServlet.doGet(request, response);
        Mockito.verify(authorService, Mockito.times(1)).getAuthorById(authorId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(response, Mockito.times(1)).sendError(500, "Server Error");
    }
}
