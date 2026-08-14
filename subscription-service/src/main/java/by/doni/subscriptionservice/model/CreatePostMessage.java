package by.doni.subscriptionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostMessage {

    private Long postId;

    private Long authorId;

    private String message;

}
