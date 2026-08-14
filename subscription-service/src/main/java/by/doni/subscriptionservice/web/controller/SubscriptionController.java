package by.doni.subscriptionservice.web.controller;

import by.doni.subscriptionservice.service.SubscriptionService;
import by.doni.subscriptionservice.web.dto.ChangeSubscriptionRequest;
import by.doni.subscriptionservice.web.dto.SubscriptionDto;
import by.doni.subscriptionservice.web.dto.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CORE_SERVICE')")
    public ResponseEntity<SubscriptionDto> changeSubscription(@RequestBody ChangeSubscriptionRequest request) {
        log.info("Get request for change subscription: {}", request);
        if (request.getSubscriptionType() == SubscriptionType.SUBSCRIBE) {
            subscriptionService.addSubscriber(request.getFolloweeId(), request.getFollowerId());
        } else {
            subscriptionService.removeSubscriber(request.getFolloweeId(), request.getFollowerId());
        }

        var updateSubscription = subscriptionService.getSubscriptionById(request.getFolloweeId());
        return ResponseEntity.ok(new SubscriptionDto(updateSubscription.getId(), updateSubscription.getSubscribersId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CORE_SERVICE')")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        log.info("Delete subscription: {}", id);
        subscriptionService.deleteSubscriptionById(id);
        return ResponseEntity.noContent().build();
    }
}
