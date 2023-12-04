package ru.marzuev.model;

import java.util.Objects;

public class Comment {
    private long id;
    private String content;
    private Book book;

    public Comment(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Comment(long id, String content, Book book) {
        this.id = id;
        this.content = content;
        this.book = book;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id && Objects.equals(content, comment.content) && Objects.equals(book, comment.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, book);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", book=" + book +
                '}';
    }
}
