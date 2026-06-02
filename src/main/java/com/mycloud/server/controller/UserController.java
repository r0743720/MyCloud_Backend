package com.mycloud.server.controller;

import com.mycloud.server.model.User;
import com.mycloud.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody Map<String, String> body) {
        if (userRepository.existsByUsername(body.get("username"))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        user.setRole(User.Role.valueOf(body.getOrDefault("role", "USER")));
        user.setStorageQuotaBytes(Long.parseLong(body.getOrDefault("quotaBytes", "10737418240")));
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/quota")
    public User updateQuota(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setStorageQuotaBytes(body.get("quotaBytes"));
        return userRepository.save(user);
    }
}