package ru.marzuev.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class BookDto {
    private String title;
    private String description;
    private LocalDate release;
    private List<Long> authors;
    private List<CommentDto> comments;

    public BookDto(String title, String description, LocalDate release, List<Long> authors,
                   List<CommentDto> comments) {
        this.title = title;
        this.description = description;
        this.release = release;
        this.authors = authors;
        this.comments = comments;
    }

    public BookDto(String title, String description, LocalDate release) {
        this.title = title;
        this.description = description;
        this.release = release;
    }

    public String getTitle() {
        return title;
    }

    public String getdescription() {
        return description;
    }

    public LocalDate getRelease() {
        return release;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public List<Long> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Long> authors) {
        this.authors = authors;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(title, bookDto.title) && Objects.equals(description, bookDto.description) &&
                Objects.equals(release, bookDto.release) && Objects.equals(authors, bookDto.authors) &&
                Objects.equals(comments, bookDto.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, release, authors, comments);
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", release=" + release +
                ", authors=" + authors +
                ", comments=" + comments +
                '}';
    }
}
