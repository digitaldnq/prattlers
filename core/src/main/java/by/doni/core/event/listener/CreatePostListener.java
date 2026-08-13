package by.doni.core.event.listener;

import by.doni.core.event.CreatePostApplicationEvent;
import by.doni.core.event.CreatePostKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostListener {

    @Value("${app.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, CreatePostKafkaEvent> createPostKafkaTemplate;

    @EventListener
    public void OnEvent(CreatePostApplicationEvent event) {
        log.info("Get event for create post {}", event);

        createPostKafkaTemplate.send(
                topic,
                new CreatePostKafkaEvent(
                        event.getPostId(),
                        event.getAutorId(),
                        event.getUsername()
                )
        );
    }

}
