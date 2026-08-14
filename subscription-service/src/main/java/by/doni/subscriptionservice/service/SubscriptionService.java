package by.doni.subscriptionservice.service;

import by.doni.subscriptionservice.entity.Subscription;
import by.doni.subscriptionservice.exception.PrattlersException;
import by.doni.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new PrattlersException("Subscription not found"));
    }

    public Subscription addSubscriber(Long id, Long subscriberId) {
        log.info("Add subscriber {} in subscription {}", subscriberId, id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElse(new Subscription(id));
        subscription.getSubscribersId().add(subscriberId);
        return subscriptionRepository.save(subscription);
    }

    public Subscription removeSubscriber(Long id, Long subscriberId) {
        log.info("Remove subscriber {} in subscription {}", subscriberId, id);
        Subscription subscription = getSubscriptionById(id);
        subscription.getSubscribersId().remove(subscriberId);
        return subscriptionRepository.save(subscription);
    }

    public void deleteSubscriptionById(Long id) {
        log.info("Delete subscription {}", id);
        var subscriptions = subscriptionRepository.findAllBySubscribersIdIn(Set.of(id));
        subscriptions.forEach(it -> it.getSubscribersId().remove(id));
        log.info("Updated subscription: {}", subscriptions);
        subscriptionRepository.saveAll(subscriptions);
        subscriptionRepository.deleteById(id);
    }
}
