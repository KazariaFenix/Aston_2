package ru.marzuev.servlet;

import com.google.gson.Gson;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.repository.impl.CommentRepositoryImpl;
import ru.marzuev.service.CommentService;
import ru.marzuev.service.impl.CommentServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

public class CommentServlet extends HttpServlet {
    private Gson gson = new Gson();
    private CommentService commentService = new CommentServiceImpl(
            new CommentRepositoryImpl(new ConnectionManagerImpl()),
            new BookRepositoryImpl(new ConnectionManagerImpl())
    );

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String responseBody;
        try {
            long bookId = Long.parseLong(request.getParameter("book"));
            responseBody = gson.toJson(commentService.getCommentsByBookId(bookId));
            response.getWriter().println(responseBody);
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Book Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        CommentDto commentDto;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            long bookId = Long.parseLong(request.getParameter("book"));
            commentDto = gson.fromJson(requestBody.toString(), CommentDto.class);
            String responseBody = gson.toJson(commentService.addComment(commentDto,
                    bookId));
            response.getWriter().println(responseBody);
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Book Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        CommentDto commentDto;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long commentId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                commentDto = gson.fromJson(requestBody.toString(), CommentDto.class);
                String responseBody = gson.toJson(commentService.updateComment(commentDto, commentId));
                response.getWriter().println(responseBody);
            } else {
                response.sendError(406, "Comment Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Comment Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long commentId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                commentService.deleteComment(commentId);
            } else {
                response.sendError(406, "Comment Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Comment Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }
}
