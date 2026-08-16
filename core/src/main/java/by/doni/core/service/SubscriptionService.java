package by.doni.core.service;

import by.doni.core.entity.Subscription;
import by.doni.core.entity.SubscriptionType;
import by.doni.core.entity.User;
import by.doni.core.event.SubscriptionChangeApplicationEvent;
import by.doni.core.exception.PrattlersException;
import by.doni.core.repository.SubscriptionRepository;
import by.doni.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final UserService userService;

    private final SubscriptionRepository subscriptionRepository;

    private final ApplicationEventPublisher publisher;

    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#followeeId")
    public void subscribe(Long followerId, Long followeeId) {

        if (followerId == null || followeeId == null) {
            throw new PrattlersException("Invalid usernames provided");
        }

        User follower = userService.findById(followerId);
        User followee = userService.findById(followeeId);

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            Subscription subscription = new Subscription();
            subscription.setFollower(follower);
            subscription.setFollowee(followee);
            subscriptionRepository.save(subscription);
            publisher.publishEvent(new SubscriptionChangeApplicationEvent(
                    this,
                    followeeId,
                    followerId,
                    SubscriptionType.SUBSCRIBE
            ));
        }
    }

    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#followeeId")
    public void unsubscribe(Long followerId, Long followeeId) {

        if (followerId == null || followeeId == null) {
            throw new PrattlersException("Invalid usernames provided");
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.info("Delete by follower {} and followee {}", followerId, followeeId);
            subscriptionRepository.deleteAllByFollowerIdOrFolloweeId(followerId, followeeId);
            publisher.publishEvent(new SubscriptionChangeApplicationEvent(
                    this,
                    followeeId,
                    followerId,
                    SubscriptionType.UNSUBSCRIBE
            ));
        }
    }

    @Cacheable(value = "userSubscriptions", key = "#followeeId")
    public List<User> getFollowers(Long followeeId) {
        return subscriptionRepository.findFollowersByFolloweeId(followeeId);
    }
}
