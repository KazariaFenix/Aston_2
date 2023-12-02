package ru.marzuev.model.mapper;

import ru.marzuev.model.Book;
import ru.marzuev.model.dto.BookDto;

import java.util.stream.Collectors;


public class BookMapper {

    public static BookDto toBookDto(Book book) {
        return new BookDto(book.getTitle(), book.getDescription(), book.getRelease(),
                book.getListAuthors().stream()
                        .map(author -> author.getId())
                        .collect(Collectors.toList()),
                book.getListComments().stream()
                        .map(comment -> CommentMapper.toCommentDto(comment))
                        .collect(Collectors.toList()));
    }

    public static Book toBook(BookDto bookDto, long bookId) {
        return new Book(bookId, bookDto.getTitle(), bookDto.getdescription(), bookDto.getRelease());
    }

}
