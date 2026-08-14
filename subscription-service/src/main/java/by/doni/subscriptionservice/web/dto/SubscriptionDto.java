package by.doni.subscriptionservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    private Long id;

    private Set<Long> subscribersId = new HashSet<>();
}
