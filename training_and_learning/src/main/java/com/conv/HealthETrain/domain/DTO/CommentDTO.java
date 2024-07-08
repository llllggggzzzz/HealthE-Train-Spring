package com.conv.HealthETrain.domain.DTO;
import com.conv.HealthETrain.domain.POJP.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentDTO {
    private Comment comment;
    private String username;
    private String cover;
}
