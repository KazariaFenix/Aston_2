package ru.marzuev.model.dto;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AuthorDto {
    private String name;
    private LocalDate dateBorn;
    private List<String> booksList;

    public AuthorDto(String name, LocalDate dateBorn, List<String> booksList) {
        this.name = name;
        this.dateBorn = dateBorn;
        this.booksList = booksList;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateBorn() {
        return dateBorn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorDto authorDto = (AuthorDto) o;
        return Objects.equals(name, authorDto.name) && Objects.equals(dateBorn, authorDto.dateBorn)
                && Objects.equals(booksList, authorDto.booksList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateBorn, booksList);
    }

    @Override
    public String toString() {
        return "AuthorDto{" +
                "name='" + name + '\'' +
                ", dateBorn=" + dateBorn +
                ", booksList=" + booksList +
                '}';
    }
}
