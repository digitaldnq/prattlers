package by.doni.core.event.listener;

import by.doni.core.event.CreatePostApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostListener {

    @EventListener
    public void OnEvent(CreatePostApplicationEvent event) {
        log.info("Get event for create post {}", event);
    }

}
