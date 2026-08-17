package by.doni.subscriptionservice.web.controller;

import by.doni.subscriptionservice.web.dto.ReadNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class NotificationController {

    @MessageMapping("/delivered")
    @SendTo("/topic/delivered")
    public String handleDeliveredMessage(@Payload ReadNotificationMessage message) {
        log.info("Received delivered notification message: {}", message);
        return "Пост с ID " + message.getPostId() + " был удален из нотификации!";
    }

}
