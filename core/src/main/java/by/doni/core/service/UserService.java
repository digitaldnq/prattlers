package by.doni.core.service;

import by.doni.core.entity.User;
import by.doni.core.exception.PrattlersException;
import by.doni.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User findById(Long id) {
        log.info("Get user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new PrattlersException("User not found"));
    }

    public User create(User user) {
        log.info("Save user with username: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
