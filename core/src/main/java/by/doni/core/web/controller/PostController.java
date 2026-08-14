package by.doni.core.web.controller;

import by.doni.core.entity.Post;
import by.doni.core.repository.specification.PostFilter;
import by.doni.core.security.AppUserDetails;
import by.doni.core.service.PostService;
import by.doni.core.web.dto.CreatePostRequest;
import by.doni.core.web.dto.PageResponse;
import by.doni.core.web.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PageResponse<PostDto>> getAllPosts(
            @RequestParam Integer pageSize,
            @RequestParam Integer pageNumber
    ) {
        return constructFromPage(postService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<PostDto>> filterPosts(PostFilter filter) {
        return constructFromPage(postService.filter(
                filter,
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PostDto> createPost(
            @RequestBody CreatePostRequest post,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var currentUserId = ((AppUserDetails) userDetails).getId();
        var createdPost = postService.create(new Post(post.getText(), post.getTag()), currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PostDto(
                        createdPost.getId(),
                        createdPost.getText(),
                        createdPost.getTag(),
                        createdPost.getAuthor().getUsername()
                ));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var currentUserId = ((AppUserDetails) userDetails).getId();
        postService.deleteById(postId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<PageResponse<PostDto>> constructFromPage(Page<Post> page) {
        var content = page.getContent().stream()
                .map(it -> new PostDto(
                        it.getId(),
                        it.getText(),
                        it.getTag(),
                        it.getAuthor().getUsername()
                ))
                .toList();
        return ResponseEntity.ok(new PageResponse<>(content, page.getTotalPages()));
    }
}
