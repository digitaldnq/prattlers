package by.doni.core.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionChangeListener {

    @EventListener
    public void OnEvent(SubscriptionChangeApplicationEvent event) {
        log.info("Get event for subscription change: {}", event);
    }

}
