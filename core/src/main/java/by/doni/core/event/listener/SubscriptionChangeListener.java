package by.doni.core.event.listener;

import by.doni.core.client.SubscriptionClient;
import by.doni.core.event.SubscriptionChangeApplicationEvent;
import by.doni.core.exception.PrattlersException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionChangeListener {

    private final SubscriptionClient client;

    @EventListener
    public void OnEvent(SubscriptionChangeApplicationEvent event) {
        log.info("Get event for subscription change: {}", event);

        switch (event.getSubscriptionType()) {
            case SUBSCRIBE -> client.subscribe(event.getFoloweeId(), event.getFollowerId());
            case UNSUBSCRIBE -> client.unsubscribe(event.getFoloweeId(), event.getFollowerId());
            case REMOVE -> client.deleteSubcription(event.getFoloweeId());
            default -> throw new PrattlersException("Type not found: " + event.getSubscriptionType());
        }
    }

}
