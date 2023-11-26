package ru.marzuev.model;


import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class Author {
    private long id;
    private String name;
    private LocalDate dateBorn;

    public Author(Long id, String name, LocalDate dateBorn) {
        this.id = id;
        this.name = name;
        this.dateBorn = dateBorn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        Author author = (Author) o;
        return id == author.id && Objects.equals(name, author.name) && Objects.equals(dateBorn, author.dateBorn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateBorn);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateBorn=" + dateBorn +
                '}';
    }
}
