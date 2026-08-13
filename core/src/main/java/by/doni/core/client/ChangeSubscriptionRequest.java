package by.doni.core.client;

import by.doni.core.entity.Subscription;
import by.doni.core.entity.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeSubscriptionRequest {

    private Long followeeId;

    private Long followerId;

    private SubscriptionType subscriptionType;
}
