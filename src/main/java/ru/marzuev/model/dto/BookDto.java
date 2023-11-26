package ru.marzuev.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class BookDto {
    private String title;
    private String descriprion;
    private LocalDate release;
    private List<Long> authors;
    private List<CommentDto> comments;

    public BookDto(String title, String descriprion, LocalDate release, List<Long> authors,
                   List<CommentDto> comments) {
        this.title = title;
        this.descriprion = descriprion;
        this.release = release;
        this.authors = authors;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public String getDescriprion() {
        return descriprion;
    }

    public LocalDate getRelease() {
        return release;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(title, bookDto.title) && Objects.equals(descriprion, bookDto.descriprion) && Objects.equals(release, bookDto.release) && Objects.equals(authors, bookDto.authors) && Objects.equals(comments, bookDto.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, descriprion, release, authors, comments);
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "title='" + title + '\'' +
                ", descriprion='" + descriprion + '\'' +
                ", release=" + release +
                ", authors=" + authors +
                ", comments=" + comments +
                '}';
    }
}
