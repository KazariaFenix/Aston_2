package ru.marzuev.model.dto;


import java.util.Objects;

public class CommentDto {
    private String titleBook;
    private String content;

    public CommentDto(String titleBook, String content) {
        this.titleBook = titleBook;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBook() {
        return titleBook;
    }

    public void setBook(String titleBook) {
        this.titleBook = titleBook;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDto that = (CommentDto) o;
        return Objects.equals(content, that.content) && Objects.equals(titleBook, that.titleBook);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, titleBook);
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "content='" + content + '\'' +
                ", book=" + titleBook +
                '}';
    }
}
