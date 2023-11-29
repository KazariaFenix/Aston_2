package testmapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.marzuev.model.Author;
import ru.marzuev.model.Book;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.AuthorDto;
import ru.marzuev.model.dto.BookDto;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.model.mapper.AuthorMapper;
import ru.marzuev.model.mapper.BookMapper;
import ru.marzuev.model.mapper.CommentMapper;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapperTest {

    private AuthorDto authorDto;
    private Author author;
    private Book book;
    private Book otherBook;
    private BookDto bookDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void createAuthorAndAuthorDto() {
        authorDto = new AuthorDto("Ivan", LocalDate.of(1990, 10, 13),
                List.of("Title", "NewTitle"));
        author = new Author(1L, "Ivan", LocalDate.of(1990, 10, 13));
        book = new Book(1L, "Title", "DESC", LocalDate.now());
        otherBook = new Book(2L, "NewTitle", "OTHER", LocalDate.now());
        comment = new Comment(1l, "Content");
        commentDto = new CommentDto("Content");
        bookDto = new BookDto("Title", "DESC", LocalDate.now(), List.of(1L), List.of(commentDto));
    }

    @Test
    void toAuthorDto_whenNormal_thenReturnAuthorDto() {
        AuthorDto newAuthor = AuthorMapper.toAuthorDto(author, List.of(book, otherBook));


        assertThat(newAuthor, equalTo(authorDto));
    }

    @Test
    void toAuthor_whenNormal_thenReturnAuthor() {
        Author newAuthor = AuthorMapper.toAuthor(1L, authorDto);

        assertThat(newAuthor, equalTo(author));
    }

    @Test
    void toBook_whenNormal_thenReturnBook() {
        Book newBook = BookMapper.toBook(bookDto, 1L);

        assertThat(book, equalTo(newBook));
    }

    @Test
    void toBookDto_whenNormal_thenReturnBookDto() {
        BookDto newBook = BookMapper.toBookDto(book, List.of(author.getId()), List.of(commentDto));

        assertThat(newBook.getComments(), equalTo(bookDto.getComments()));
        assertThat(newBook, equalTo(bookDto));
    }

    @Test
    void toComment_whenNormal_thenReturnComment() {
        Comment newComment = CommentMapper.toComment(commentDto, 1L);

        assertThat(comment.getId(), equalTo(newComment.getId()));
        assertThat(comment.getAuthor(), equalTo(newComment.getAuthor()));
        assertThat(comment.getBook(), equalTo(newComment.getBook()));
        assertThat(comment.getContent(), equalTo(newComment.getContent()));
        assertThat(comment, equalTo(newComment));
    }

    @Test
    void toCommentDto_whenNormal_thenReturnCommentDto() {
        CommentDto newComment = CommentMapper.toCommentDto(comment);

        assertThat(newComment.getContent(), equalTo(commentDto.getContent()));
        assertThat(newComment, equalTo(commentDto));
    }
}
