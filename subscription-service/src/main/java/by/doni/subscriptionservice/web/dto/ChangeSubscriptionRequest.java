package by.doni.subscriptionservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeSubscriptionRequest {

    @NotNull
    private Long followeeId;

    @NotNull
    private Long followerId;

    @NotNull
    private SubscriptionType subscriptionType;
}
