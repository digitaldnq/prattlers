package by.doni.core.web.controller;

import by.doni.core.entity.User;
import by.doni.core.service.UserService;
import by.doni.core.web.dto.CreateUserRequest;
import by.doni.core.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        var user = userService.findById(userId);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        var user = new User(request.getUsername(), request.getPassword(), request.getRole());
        var createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserDto(createdUser.getId(), createdUser.getUsername()));
    }

    @DeleteMapping("/{userId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}
