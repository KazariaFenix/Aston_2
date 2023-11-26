package ru.marzuev.db;

public class InitDatabaseImpl {

    public static String createTable() {
        return "CREATE TABLE IF NOT EXISTS authors(\n" +
                "    author_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
                "    name varchar(256) UNIQUE NOT NULL,\n" +
                "    date_born date NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS books(\n" +
                "    book_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
                "    title varchar(256) UNIQUE NOT NULL,\n" +
                "    description varchar(1024) NOT NULL,\n" +
                "    release date NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS comments(\n" +
                "    comment_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
                "    content varchar(1024) NOT NULL,\n" +
                "    book_id bigint REFERENCES books ON DELETE CASCADE\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS authors_books(\n" +
                "    author_id bigint REFERENCES authors ON DELETE CASCADE,\n" +
                "    book_id bigint REFERENCES books ON DELETE CASCADE,\n" +
                "    CONSTRAINT pk_authors_books PRIMARY KEY (author_id, book_id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS books_comments(\n" +
                "    book_id bigint REFERENCES books ON DELETE CASCADE,\n" +
                "    comment_id bigint REFERENCES comments ON DELETE CASCADE,\n" +
                "    CONSTRAINT pk_books_comments PRIMARY KEY (book_id, comment_id)\n" +
                ");";
    }
}
