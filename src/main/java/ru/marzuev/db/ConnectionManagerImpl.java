package ru.marzuev.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class ConnectionManagerImpl implements ConnectionManager {

    public ConnectionManagerImpl() {
    }

    private final Path path = Paths.get("src/main/resources/db.properties");

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            List<String> properties = Files.readAllLines(path);
            connection = DriverManager.getConnection(properties.get(0), properties.get(1),
                    properties.get(2));
        } catch (IOException e) {
            e.getMessage();
        }
        return connection;
    }
}
