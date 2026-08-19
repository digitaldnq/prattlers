package by.doni.core.web.controller;

import by.doni.core.client.ChangeSubscriptionRequest;
import by.doni.core.entity.SubscriptionType;
import by.doni.core.repository.SubscriptionRepository;
import by.doni.core.service.SubscriptionService;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestSubscriptionController extends AbstractWebTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    @WithUserDetails("test_user1")
    public void whenSendSubscribeOnUserThenSaveSubscriptionAndSendRequestToSubscriptionService() throws Exception {
        assertFalse(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L));

        mockMvc.perform(post("/api/v1/subscriptions/subscribe")
                        .param("followeeId", "2"))
                .andExpect(status().isNoContent());

        assertTrue(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L));

        wireMockServer.verify(exactly(1), WireMock.postRequestedFor(WireMock.urlEqualTo("/api/v1/subscriptions"))
                .withBasicAuth(new BasicCredentials(clientUser, clientPassword))
                .withRequestBody(
                        equalToJson(
                                objectMapper.writeValueAsString(
                                        new ChangeSubscriptionRequest(2L, 1L, SubscriptionType.SUBSCRIBE)
                                )
                        )
                ));
    }

    @Test
    @WithUserDetails("test_user1")
    public void whenSendUnsubscribeOnUserThenRemoveSubscriptionsAndSendRequestToSubscriptionService() throws Exception {
        subscriptionService.subscribe(1L, 2L);
        assertTrue(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L));

        mockMvc.perform(post("/api/v1/subscriptions/unsubscribe")
                        .param("followeeId", "2"))
                .andExpect(status().isNoContent());

        assertFalse(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L));

        wireMockServer.verify(exactly(1), WireMock.postRequestedFor(WireMock.urlEqualTo("/api/v1/subscriptions"))
                .withBasicAuth(new BasicCredentials(clientUser, clientPassword))
                .withRequestBody(
                        equalToJson(
                                objectMapper.writeValueAsString(
                                        new ChangeSubscriptionRequest(2L, 1L, SubscriptionType.UNSUBSCRIBE)
                                )
                        )
                ));
    }

    @Test
    public void whenGetAllSubscriptionsBySubscriptionThenReturnListOfSubscribers() throws Exception {
        subscriptionService.subscribe(1L, 2L);
        mockMvc.perform(get("/api/v1/subscriptions/followers")
                        .param("followeeId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].username").value(equalTo("test_user1")));
    }

}
