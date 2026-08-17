package by.doni.core.service;

import by.doni.core.entity.Post;
import by.doni.core.event.CreatePostApplicationEvent;
import by.doni.core.exception.PrattlersException;
import by.doni.core.repository.PostRepository;
import by.doni.core.repository.specification.PostFilter;
import by.doni.core.repository.specification.PostSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    private final ApplicationEventPublisher publisher;

    @Cacheable(
            value = "posts",
            key = "new org.springframework.cache.interceptor.SimpleKey(#pageable.pageNumber, #pageable.pageSize)"
    )
    public Page<Post> findAll(Pageable pageable) {
        log.info("Find all by pageable: {}", pageable);
        return postRepository.findAll(pageable);
    }

    public Page<Post> filter(PostFilter filter, Pageable pageable) {
        log.info("Filter posts by data: {}", filter);
        return postRepository.findAll(PostSpecification.withFilter(filter), pageable);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public Post create(Post post, Long authorId) {
        log.info("Create new post");
        var author = userService.findById(authorId);
        post.setAuthor(author);
        var newPost = postRepository.save(post);
        publisher.publishEvent(new CreatePostApplicationEvent(
                this,
                newPost.getId(),
                authorId,
                author.getUsername()
        ));
        return newPost;
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public void deleteById(Long postId, Long userId) {
        log.info("Delete post by id: {}", postId);
        if (!postRepository.existsByAuthorId(userId)) {
            log.error("Exception on delete. PostId: {}, UserId: {}", postId, userId);
            throw new PrattlersException("Exception trying to delete post with id: " + postId);
        }
        postRepository.deleteById(postId);
    }
}
