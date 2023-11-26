package testservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.CommentDto;
import ru.marzuev.repository.BookRepository;
import ru.marzuev.repository.CommentRepository;
import ru.marzuev.service.impl.CommentServiceImpl;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookRepository bookRepository;

    @Test
    void addComment_whenNormal_thenReturnComment() throws SQLException {
        final Comment comment = new Comment(0, "Content");
        final Comment postComment = new Comment(1, "Content");
        final CommentDto commentDto = new CommentDto("Content");
        final CommentDto addComment = new CommentDto("Content");
        final long bookId = 1L;
        Mockito
                .when(commentRepository.addComment(comment, bookId))
                .thenReturn(postComment);

        CommentDto postCommentDto = commentService.addComment(commentDto, bookId);
        assertThat(addComment, equalTo(postCommentDto));
        Mockito.verify(commentRepository, Mockito.times(1)).addComment(comment, bookId);
    }

    @Test
    void addComment_whenBookNotFound_thenThrowException() throws SQLException {
        final CommentDto commentDto = new CommentDto("Content");
        final long bookId = 1L;
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenThrow(new SQLException("Book Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> commentService.addComment(commentDto, bookId));
        assertThat(e.getMessage(), equalTo("Book Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1)).getBookById(bookId);
    }

    @Test
    void updateComment_whenNormal_thenReturnComment() throws SQLException {
        final Comment comment = new Comment(1, "Content");
        final Comment updateComment = new Comment(1, "Content");
        final CommentDto commentDto = new CommentDto("Content");
        final CommentDto addComment = new CommentDto("Content");
        Mockito
                .when(commentRepository.updateComment(comment, comment.getId()))
                .thenReturn(updateComment);

        CommentDto updateCommentDto = commentService.updateComment(commentDto, comment.getId());
        assertThat(addComment, equalTo(updateCommentDto));
        Mockito.verify(commentRepository, Mockito.times(1))
                .updateComment(comment, comment.getId());
    }

    @Test
    void updateComment_whenCommentNotFound_thenThrowException() throws SQLException {
        final CommentDto commentDto = new CommentDto("Content");
        final long commentId = 1L;
        Mockito
                .when(commentRepository.getCommentById(commentId))
                .thenThrow(new SQLException("Comment Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> commentService.updateComment(commentDto, commentId));
        assertThat(e.getMessage(), equalTo("Comment Not Found"));
        Mockito.verify(commentRepository, Mockito.times(1)).getCommentById(commentId);
    }

    @Test
    void deleteCommentById_whenNormal_thenDeleteComment() throws SQLException {
        final long commentId = 1L;
        Mockito
                .doNothing()
                .when(commentRepository).deleteComment(commentId);
        commentService.deleteComment(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).deleteComment(commentId);
    }

    @Test
    void deleteCommentById_whenCommentNotFound_thenThrowException() throws SQLException {
        final long commentId = 1L;
        Mockito
                .doThrow(new SQLException("Comment Not Found"))
                .when(commentRepository).getCommentById(commentId);
        SQLException e = assertThrows(SQLException.class,
                () -> commentService.deleteComment(commentId));
        assertThat(e.getMessage(), equalTo("Comment Not Found"));
        Mockito.verify(commentRepository, Mockito.times(1)).getCommentById(commentId);
    }

    @Test
    void getCommentsByBook_whenNormal_thenReturnComments() throws SQLException {
        final long bookId = 1L;
        final Comment comment = new Comment(1, "Content");
        final Comment comment1 = new Comment(2, "Content");
        Mockito
                .when(commentRepository.getCommentsByBookId(bookId))
                .thenReturn(List.of(comment, comment1));

        List<CommentDto> commentsDtoByBooks = commentService.getCommentsByBookId(bookId);
        assertThat(commentsDtoByBooks.size(), equalTo(2));
        Mockito.verify(commentRepository, Mockito.times(1)).getCommentsByBookId(bookId);
    }

    @Test
    void getCommentsByBookId_whenBookNotFound_thenThrowException() throws SQLException {
        final long bookId = 1L;
        Mockito
                .when(bookRepository.getBookById(bookId))
                .thenThrow(new SQLException("Book Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> commentService.getCommentsByBookId(bookId));
        assertThat(e.getMessage(), equalTo("Book Not Found"));
        Mockito.verify(bookRepository, Mockito.times(1)).getBookById(bookId);
    }
}
