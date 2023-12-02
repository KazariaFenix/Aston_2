package ru.marzuev.model;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Author {
    private long id;
    private String name;
    private LocalDate dateBorn;

    private List<Book> listBooks;

    public Author(Long id, String name, LocalDate dateBorn) {
        this.id = id;
        this.name = name;
        this.dateBorn = dateBorn;
    }

    public List<Book> getListBooks() {
        return listBooks;
    }

    public void setListBooks(List<Book> listBooks) {
        this.listBooks = listBooks;
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
        return id == author.id && Objects.equals(name, author.name) && Objects.equals(dateBorn, author.dateBorn)
                && Objects.equals(listBooks, author.listBooks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateBorn, listBooks);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateBorn=" + dateBorn +
                ", listBooks=" + listBooks +
                '}';
    }
}
