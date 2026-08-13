package by.doni.core.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeSubscriptionResponse {

    private Long id;

    private Set<Long> subscriberIds = new HashSet<>();
}
