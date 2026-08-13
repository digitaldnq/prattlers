package by.doni.core.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class CreatePostApplicationEvent extends ApplicationEvent {

    private final Long postId;

    private final Long autorId;

    private final String username;


    public CreatePostApplicationEvent(Object source, Long postId, Long autorId, String username) {
        super(source);
        this.postId = postId;
        this.autorId = autorId;
        this.username = username;
    }
}
