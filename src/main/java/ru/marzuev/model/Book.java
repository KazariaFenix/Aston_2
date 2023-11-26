package ru.marzuev.model;


import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private long id;
    private String title;
    private String description;
    private LocalDate release;

    public Book(long id, String title, String description, LocalDate release) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.release = release;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getRelease() {
        return release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && Objects.equals(title, book.title) && Objects.equals(description, book.description)
                && Objects.equals(release, book.release);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, release);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", release=" + release +
                '}';
    }
}
