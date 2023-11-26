package ru.marzuev.servlet;

import com.google.gson.Gson;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.repository.impl.CommentRepositoryImpl;
import ru.marzuev.service.BookService;
import ru.marzuev.service.impl.BookServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private BookService bookService = new BookServiceImpl(new BookRepositoryImpl(new ConnectionManagerImpl()),
            new AuthorRepositoryImpl(new ConnectionManagerImpl()),
            new CommentRepositoryImpl(new ConnectionManagerImpl()));

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String responseBody;
        try {
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int index = request.getRequestURI().lastIndexOf("/");
                long bookId = Long.parseLong(request.getRequestURI().substring(index + 1));
                responseBody = gson.toJson(bookService.getBookById(bookId));
                response.getWriter().println(responseBody);
                return;
            }
            if (request.getParameter("author") != null) {
                long authorId = Long.parseLong(request.getParameter("author"));
                responseBody = gson.toJson(bookService.getBooksByAuthorId(authorId));
                response.getWriter().println(responseBody);
            } else {
                response.sendError(406, "Insert Author Id Or Book Id");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Book Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BookDto bookDto;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            String authors = request.getParameter("author");
            String[] authorsId = authors.split(",");
            List<Long> authorsList = Arrays.stream(authorsId)
                    .map(authorId -> Long.parseLong(authorId))
                    .collect(Collectors.toList());
            bookDto = gson.fromJson(requestBody.toString(), BookDto.class);
            String responseBody = gson.toJson(bookService.addBook(bookDto, authorsList));
            response.getWriter().println(responseBody);
        } catch (IllegalArgumentException e) {
            response.sendError(409, "Author Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BookDto bookDto;
        String responseBody;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long bookId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                bookDto = gson.fromJson(requestBody.toString(), BookDto.class);
                responseBody = gson.toJson(bookService.updateBook(bookDto, bookId));
                response.getWriter().println(responseBody);
            } else {
                response.sendError(406, "Book Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Book Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long bookId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                bookService.deleteBook(bookId);
            } else {
                response.sendError(406, "Book Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Book Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }
}
