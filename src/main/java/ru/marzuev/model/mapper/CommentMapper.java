package ru.marzuev.model.mapper;

import ru.marzuev.model.Comment;
import ru.marzuev.model.dto.CommentDto;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getContent());
    }

    public static Comment toComment(CommentDto commentDto, long commentId) {
        return new Comment(commentId, commentDto.getContent());
    }
}
