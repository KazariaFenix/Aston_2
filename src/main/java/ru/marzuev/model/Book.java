package ru.marzuev.model;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Book {
    private long id;
    private String title;
    private String description;
    private LocalDate release;
    private List<Author> listAuthors;
    private List<Comment> listComments;


    public Book(long id, String title, String description, LocalDate release) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.release = release;
    }

    public Book(long id, String title, String description, LocalDate release, List<Author> listAuthors,
                List<Comment> listComments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.release = release;
        this.listAuthors = listAuthors;
        this.listComments = listComments;
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

    public List<Author> getListAuthors() {
        return listAuthors;
    }

    public void setListAuthors(List<Author> listAuthors) {
        this.listAuthors = listAuthors;
    }

    public List<Comment> getListComments() {
        return listComments;
    }

    public void setListComments(List<Comment> listComments) {
        this.listComments = listComments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && Objects.equals(title, book.title) && Objects.equals(description, book.description) &&
                Objects.equals(release, book.release) && Objects.equals(listAuthors, book.listAuthors) &&
                Objects.equals(listComments, book.listComments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, release, listAuthors, listComments);
    }


}
