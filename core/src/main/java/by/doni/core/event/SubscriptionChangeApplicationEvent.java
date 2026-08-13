package by.doni.core.event;

import by.doni.core.entity.SubscriptionType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class SubscriptionChangeApplicationEvent extends ApplicationEvent {

    private final Long foloweeId;

    private final Long followerId;

    private final SubscriptionType subscriptionType;

    public SubscriptionChangeApplicationEvent(
            Object source,
            Long foloweeId,
            Long followerId,
            SubscriptionType subscriptionType
    ) {
        super(source);
        this.foloweeId = foloweeId;
        this.followerId = followerId;
        this.subscriptionType = subscriptionType;
    }
}
