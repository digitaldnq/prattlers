package by.doni.subscriptionservice.listener;

import by.doni.subscriptionservice.model.CreatePostKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostKafkaListener {

    @KafkaListener(topics = "${app.kafka.topic}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "createPostKafkaListenerContainerFactory")
    public void listen(@Payload CreatePostKafkaEvent event) {
        log.info("Received create post event {}", event);
    }

}
