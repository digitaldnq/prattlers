package by.doni.core.web.controller;

import by.doni.core.entity.Post;
import by.doni.core.entity.User;
import by.doni.core.event.CreatePostKafkaEvent;
import by.doni.core.repository.PostRepository;
import by.doni.core.web.dto.CreatePostRequest;
import by.doni.core.web.dto.PostDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestPostController extends AbstractWebTest {

    @Value("${app.kafka.topic}")
    private String kafkaTopic;

    @Autowired
    private PostRepository postRepository;

    @Test
    @WithUserDetails("test_user1")
    public void whenCreatePostThenSaveInDbAndSendKafkaMessage() throws Exception {
        try (var consumer = getTestConsumer()) {
            consumer.subscribe(List.of(kafkaTopic));

            var response = mockMvc.perform(post("/api/v1/posts")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(new CreatePostRequest("post text", "#test_tag"))))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var expectedPost = new PostDto(1L, "post text", "#test_tag", "test_user1");
            var createdPost = objectMapper.readValue(response, PostDto.class);
            assertEquals(expectedPost, createdPost);
            assertTrue(postRepository.findById(createdPost.getId()).isPresent());

            var messageToSend = new CreatePostKafkaEvent(
                    createdPost.getId(),
                    userRepository.findByUsername("test_user1").get().getId(),
                    createdPost.getAuthor()
            );

            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .until(() -> {
                        for (ConsumerRecord<String, CreatePostKafkaEvent> record : consumer.poll(Duration.ofMillis(100))) {
                            if (record.value().equals(messageToSend)) {
                                return true;
                            }
                        }

                        return false;
                    });
        }
    }

    @Test
    public void whenFindAllPostsByPagesThenReturnPostsWithTotalPageInfo() throws Exception {
        List<Post> posts = new ArrayList<>();
        var user = userRepository.findByUsername("test_user1").get();
        for (int i = 0; i < 100; i++) {
            posts.add(creatPost(user));
        }
        postRepository.saveAll(posts);

        mockMvc.perform(get("/api/v1/posts")
                        .param("pageSize", "10")
                        .param("pageNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages", is(10)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.data[*].id").exists())
                .andExpect(jsonPath("$.data[*].text").exists())
                .andExpect(jsonPath("$.data[*].tag").exists())
                .andExpect(jsonPath("$.data[*].author").exists());
    }

    @Test
    public void whenFilterByAuthorThenReturnPostsByAuthor() throws Exception {
        var firstUser = userRepository.findByUsername("test_user1").get();
        var secondUser = userRepository.findByUsername("test_user2").get();
        List<Post> firstUserPosts = new ArrayList<>();
        List<Post> secondUserPosts = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            firstUserPosts.add(creatPost(firstUser));
        }

        for (int i = 0; i < 45; i++) {
            secondUserPosts.add(creatPost(secondUser));
        }

        postRepository.saveAll(firstUserPosts);
        postRepository.saveAll(secondUserPosts);

        mockMvc.perform(get("/api/v1/posts/filter")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .param("authorId", firstUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[*].id").exists())
                .andExpect(jsonPath("$.data[*].text").exists())
                .andExpect(jsonPath("$.data[*].tag").exists())
                .andExpect(jsonPath("$.data[*].author").exists())
                .andExpect(jsonPath("$.data[0].author", is(firstUser.getUsername())))
                .andExpect(jsonPath("$.data[1].author", is(firstUser.getUsername())))
                .andExpect(jsonPath("$.data[2].author", is(firstUser.getUsername())))
                .andExpect(jsonPath("$.data[3].author", is(firstUser.getUsername())))
                .andExpect(jsonPath("$.data[4].author", is(firstUser.getUsername())));
    }

    @Test
    @WithUserDetails("test_user1")
    public void whenDeletePostByIdThenDeleteItFromDb() throws Exception {
        var author = userRepository.findByUsername("test_user1").get();
        var post = postRepository.save(creatPost(author));
        assertTrue(postRepository.findById(post.getId()).isPresent());

        mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId()))
                .andExpect(status().isNoContent());

        assertTrue(postRepository.findById(post.getId()).isEmpty());
    }

    @Test
    public void whenDeletePostWithoutAuthThenReturnUnauthorized() throws Exception {
        var author = userRepository.findByUsername("test_user1").get();
        var post = postRepository.save(creatPost(author));
        assertTrue(postRepository.findById(post.getId()).isPresent());

        mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId()))
                .andExpect(status().isUnauthorized());

        assertTrue(postRepository.findById(post.getId()).isPresent());
    }

    @Test
    @WithUserDetails("test_user1")
    public void whenTryToDeletePostByNotAuthorThenReturnErrorResponse() throws Exception {
        var author = userRepository.findByUsername("test_user2").get();
        var post = postRepository.save(creatPost(author));

        assertTrue(postRepository.findById(post.getId()).isPresent());

        mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(
                        jsonPath("$.message", is("Exception trying to delete post with id: " + post.getId()))
                );

        assertTrue(postRepository.findById(post.getId()).isPresent());
    }

    private Consumer<String, CreatePostKafkaEvent> getTestConsumer() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CreatePostKafkaEvent.class.getName());
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<String, CreatePostKafkaEvent>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>()
        ).createConsumer();
    }

    private Post creatPost(User author) {
        int random = new Random().nextInt(1000);
        var post = new Post("description " + random, "#tag_" + random);
        post.setAuthor(author);
        return post;
    }

}