package ru.marzuev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.marzuev.db.ConnectionManagerImpl;
import ru.marzuev.model.dto.AuthorDto;
import ru.marzuev.repository.impl.AuthorRepositoryImpl;
import ru.marzuev.repository.impl.BookRepositoryImpl;
import ru.marzuev.service.AuthorService;
import ru.marzuev.service.impl.AuthorServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

public class AuthorServlet extends HttpServlet {

    private AuthorService authorService = new AuthorServiceImpl(new AuthorRepositoryImpl(new ConnectionManagerImpl()),
            new BookRepositoryImpl(new ConnectionManagerImpl()));
    private ObjectMapper om = new ObjectMapper().findAndRegisterModules();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String responseBody;
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long authorId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                responseBody = om.writeValueAsString(authorService.getAuthorById(authorId));
                response.getWriter().println(responseBody);
            } else {
                responseBody = om.writeValueAsString(authorService.getAuthors());
                response.getWriter().println(responseBody);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Author Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        AuthorDto authorDto;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            authorDto = om.readValue(requestBody.toString(), AuthorDto.class);
            String responseBody = om.writeValueAsString(authorService.addAuthor(authorDto));
            response.getWriter().println(responseBody);
        } catch (IllegalArgumentException e) {
            response.sendError(409, "Author Already Exists");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        AuthorDto authorDto;
        try (BufferedReader bf = request.getReader()) {
            if (bf.ready()) {
                requestBody.append(bf.readLine());
            }
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long authorId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                authorDto = om.readValue(requestBody.toString(), AuthorDto.class);
                response.getWriter().println(om.writeValueAsString(authorService.updateAuthor(authorDto,
                        authorId)));
            } else {
                response.sendError(406, "Author Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Author Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!request.getServletPath().equals(request.getRequestURI())) {
                int lastIndex = request.getRequestURI().lastIndexOf("/");
                long authorId = Long.parseLong(request.getRequestURI().substring(lastIndex + 1));
                authorService.deleteAuthor(authorId);
            } else {
                response.sendError(406, "Author Id Invalid");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(404, "Author Not Found");
        } catch (SQLException e) {
            response.sendError(500, "Server Error");
        }
    }
}
