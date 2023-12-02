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
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.service.CommentService;
import ru.marzuev.servlet.CommentServlet;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentServletTest {
    private Gson gson = new Gson();
    @InjectMocks
    private CommentServlet commentServlet;
    @Mock
    private CommentService commentService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnCommentDto() throws IOException, SQLException {
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        final long bookId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("book");
            Mockito
                    .doReturn(commentDto)
                    .when(commentService).addComment(commentDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            commentServlet.doPost(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).addComment(commentDto, bookId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenBookNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        final long bookId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(commentService).addComment(commentDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("book");

            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Book Not Found");

            commentServlet.doPost(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).addComment(commentDto, bookId);
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void doPost_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        final long bookId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(commentService).addComment(commentDto, bookId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn("1")
                    .when(request).getParameter("book");

            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");
            commentServlet.doPost(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).addComment(commentDto, bookId);
        Mockito.verify(response, Mockito.never()).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
    }

    @Test
    void updateComment_whenNormal_thenReturnAuthor() throws IOException, SQLException {
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        final long commentId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/comments/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/comments")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(commentDto)
                    .when(commentService).updateComment(commentDto, commentId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            commentServlet.doPut(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).updateComment(commentDto, commentId);
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateComment_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long commentId = 1;
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/comments")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/comments")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();

            Mockito
                    .doNothing()
                    .when(response).sendError(406, "Comment Id Invalid");

            commentServlet.doPut(request, response);
        }
        Mockito.verify(commentService, Mockito.never()).updateComment(commentDto, commentId);
        Mockito.verify(response, Mockito.times(1))
                .sendError(406, "Comment Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
    }

    @Test
    void updateComment_whenCommentNotFound_throwException() throws IOException, SQLException {
        final long commentId = 1;
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/comments/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/comments")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Comment Not Found"))
                    .when(commentService).updateComment(commentDto, commentId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Comment Not Found");

            commentServlet.doPut(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).updateComment(commentDto, commentId);
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void updateComment_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long commentId = 1;
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final String responseBody = gson.toJson(commentDto);
        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/books/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/books")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(commentService).updateComment(commentDto, commentId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            commentServlet.doPut(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).updateComment(commentDto, commentId);
        Mockito.verify(request, Mockito.times(1)).getReader();
        Mockito.verify(request, Mockito.times(1)).getServletPath();
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteComment_whenNormal_thenDeleteComment() throws SQLException, IOException {
        final long commentId = 1;
        Mockito
                .doNothing()
                .when(commentService).deleteComment(commentId);
        Mockito
                .doReturn("/comments/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/comments")
                .when(request).getServletPath();
        commentServlet.doDelete(request, response);
        Mockito.verify(commentService, Mockito.times(1)).deleteComment(commentId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
    }

    @Test
    void deleteComment_whenCommentNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long commentId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(commentService).deleteComment(commentId);
        Mockito
                .doReturn("/comments/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/comments")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(404, "Comment Not Found");
        commentServlet.doDelete(request, response);
        Mockito.verify(commentService, Mockito.times(1)).deleteComment(commentId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(404, "Comment Not Found");
    }

    @Test
    void deleteComment_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long commentId = 1;
        Mockito
                .doThrow(new SQLException())
                .when(commentService).deleteComment(commentId);
        Mockito
                .doReturn("/comments/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/comments")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        commentServlet.doDelete(request, response);
        Mockito.verify(commentService, Mockito.times(1)).deleteComment(commentId);
        Mockito.verify(request, Mockito.times(3)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(500, "Server Error");
    }

    @Test
    void deleteComment_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing()
                .when(response).sendError(406, "Comment Id Invalid");
        Mockito
                .doReturn("/comments")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/comments")
                .when(request).getServletPath();
        commentServlet.doDelete(request, response);
        Mockito.verify(request, Mockito.times(1)).getRequestURI();
        Mockito.verify(response, Mockito.times(1)).sendError(406, "Comment Id Invalid");
        Mockito.verify(request, Mockito.times(1)).getServletPath();
    }

    @Test
    void getCommentsByBookId_whenNormal_thenReturnBooks() throws IOException, SQLException {
        final long bookId = 1;
        final CommentDto commentDto = new CommentDto("Title", "Content");
        final CommentDto commentDto1 = new CommentDto("Title", "Content1");

        String responseBody = gson.toJson(List.of(commentDto, commentDto1));
        Mockito
                .doReturn("1")
                .when(request).getParameter("book");
        Mockito
                .doReturn(List.of(commentDto, commentDto1))
                .when(commentService).getCommentsByBookId(bookId);
        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            commentServlet.doGet(request, response);
        }
        Mockito.verify(commentService, Mockito.times(1)).getCommentsByBookId(bookId);
        Mockito.verify(request, Mockito.times(1)).getParameter("book");
        Mockito.verify(response, Mockito.times(1)).getWriter();
    }

    @Test
    void getCommentsByBookId_whenBookNotFound_throwException() throws IOException, SQLException {
        final long bookId = 1;

        Mockito
                .doReturn("1")
                .when(request).getParameter("book");
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(commentService).getCommentsByBookId(bookId);
        Mockito
                .doNothing()
                .when(response).sendError(404, "Book Not Found");
        commentServlet.doGet(request, response);
        Mockito.verify(commentService, Mockito.times(1)).getCommentsByBookId(bookId);
        Mockito.verify(request, Mockito.times(1)).getParameter("book");
        Mockito.verify(response, Mockito.times(1))
                .sendError(404, "Book Not Found");
    }

    @Test
    void getCommentsByBookId_whenDatabaseError_throwException() throws IOException, SQLException {
        final long bookId = 1;

        Mockito
                .doReturn("1")
                .when(request).getParameter("book");
        Mockito
                .doThrow(new SQLException())
                .when(commentService).getCommentsByBookId(bookId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");
        commentServlet.doGet(request, response);
        Mockito.verify(commentService, Mockito.times(1)).getCommentsByBookId(bookId);
        Mockito.verify(request, Mockito.times(1)).getParameter("book");
        Mockito.verify(response, Mockito.times(1))
                .sendError(500, "Server Error");
    }
}