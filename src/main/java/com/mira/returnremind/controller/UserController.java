package com.mira.returnremind.controller;

import com.mira.returnremind.model.User;
import com.mira.returnremind.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record CreateUserRequest(String name, String email) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest req) {
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        return userRepository.save(u);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}