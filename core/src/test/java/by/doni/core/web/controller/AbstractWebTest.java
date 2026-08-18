package by.doni.core.web.controller;

import by.doni.core.AbstractTest;
import by.doni.core.client.ChangeSubscriptionRequest;
import by.doni.core.client.ChangeSubscriptionResponse;
import by.doni.core.entity.SubscriptionType;
import by.doni.core.repository.UserRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.Objects;
import java.util.Set;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Transactional
@AutoConfigureMockMvc
@Sql("classpath:db/init_test_data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class AbstractWebTest extends AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Value("${app.client.subscription-service.username}")
    protected String clientUser;

    @Value("${app.client.subscription-service.password}")
    protected String password;

    @RegisterExtension
    protected static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.subscription-service.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection().serverCommands().flushAll();

        stubClient();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }

    public void stubClient() throws Exception {
        wireMockServer.stubFor(WireMock.post("/api/v1/subscriptions")
                .withRequestBody(WireMock.equalToJson(getSubscriptionRequestBody(SubscriptionType.SUBSCRIBE)))
                .withBasicAuth(clientUser, password)
                .willReturn(
                        WireMock.aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(getSubscriptionResponseBody(Set.of(2L)))
                                .withStatus(HttpStatus.OK.value())
                ));

        wireMockServer.stubFor(WireMock.post("/api/v1/subscriptions")
                .withRequestBody(WireMock.equalToJson(getSubscriptionRequestBody(SubscriptionType.UNSUBSCRIBE)))
                .withBasicAuth(clientUser, password)
                .willReturn(
                        WireMock.aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(getSubscriptionResponseBody(Set.of()))
                                .withStatus(HttpStatus.OK.value())
                ));

        wireMockServer.stubFor(WireMock.delete("/api/v1/subscriptions/1")
                .withBasicAuth(clientUser, password)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NO_CONTENT.value())));
    }

    private String getSubscriptionRequestBody(SubscriptionType type) throws Exception {
        return objectMapper.writeValueAsString(
                new ChangeSubscriptionRequest(2L, 1L, type)
        );
    }

    private String getSubscriptionResponseBody(Set<Long> subscriptionIds) throws Exception {
        return objectMapper.writeValueAsString(
                new ChangeSubscriptionResponse(2L, subscriptionIds)
        );
    }
}
