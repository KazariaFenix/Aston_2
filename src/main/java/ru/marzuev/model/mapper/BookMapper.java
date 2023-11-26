package ru.marzuev.model.mapper;

import ru.marzuev.model.Book;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.model.dto.CommentDto;

import java.util.List;


public class BookMapper {

    public static BookDto toBookDto(Book book, List<Long> authors, List<CommentDto> comments) {
        return new BookDto(book.getTitle(), book.getDescription(), book.getRelease(),
                authors, comments);
    }

    public static Book toBook(BookDto bookDto, long bookId) {
        return new Book(bookId, bookDto.getTitle(), bookDto.getDescriprion(), bookDto.getRelease());
    }
}
