package by.doni.subscriptionservice.listener;

import by.doni.subscriptionservice.model.CreatePostKafkaEvent;
import by.doni.subscriptionservice.model.CreatePostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostKafkaListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${app.kafka.topic}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "createPostKafkaListenerContainerFactory")
    public void listen(@Payload CreatePostKafkaEvent event) {
        log.info("Received create post event {}", event);
        var userMessage = MessageFormat.format("Автор {0} создал новый пост", event.getUsername());
        var message = new CreatePostMessage(event.getPostId(), event.getAuthorId(), userMessage);

        messagingTemplate.convertAndSend("/topic/messages", message);
    }

}
