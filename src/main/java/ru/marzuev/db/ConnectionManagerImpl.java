package ru.marzuev.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class ConnectionManagerImpl implements ConnectionManager {
    private final Path path = Paths.get("src/main/resources/db.properties");
    private String url;
    private String login;
    private String password;

    public ConnectionManagerImpl() {
    }

    public ConnectionManagerImpl(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        if (url == null || login == null || password == null) {
            try {
                List<String> properties = Files.readAllLines(path);
                connection = DriverManager.getConnection(properties.get(0), properties.get(1),
                        properties.get(2));
            } catch (IOException e) {
                e.getMessage();
            }
        } else {
            connection = DriverManager.getConnection(url, login, password);
        }
        return connection;
    }
}
