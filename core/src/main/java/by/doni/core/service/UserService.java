package by.doni.core.service;

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
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SubscriptionRepository subscriptionRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Cacheable(value = "userById", key = "#userId")
    public User findById(Long userId) {
        log.info("Get user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new PrattlersException("User not found"));
    }

    public User create(User user) {
        log.info("Save user with username: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Caching(evict = {
            @CacheEvict(value = "userById", key = "#userId"),
            @CacheEvict(value = "userSubscriptions", allEntries = true)
    })
    @Transactional
    public void deleteById(Long userId) {
        log.info("Delete user by id: {}", userId);
        int countOfDelete = subscriptionRepository.deleteAllByFollowerIdOrFolloweeId(userId, userId);
        log.info("Delete subscriptions count: {}", countOfDelete);

        eventPublisher.publishEvent(new SubscriptionChangeApplicationEvent(this, userId, userId, SubscriptionType.REMOVE));

        userRepository.deleteById(userId);
    }
}
