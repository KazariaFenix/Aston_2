package ru.marzuev.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManagerTest implements ConnectionManager {
    private String url;
    private String login;
    private String password;

    public ConnectionManagerTest(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, login, password);
        return connection;
    }
}
