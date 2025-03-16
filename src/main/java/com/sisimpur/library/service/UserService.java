package com.sisimpur.library.service;

import com.sisimpur.library.model.User;
import com.sisimpur.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        // Validate name
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name cannot be empty.");
        }

        // Validate email (if provided)
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail().trim());
            if (existingUser.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
            }
        } else {
            user.setEmail(null); // Normalize empty string to null
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        // Update name if provided and not empty
        if (updatedUser.getName() != null && !updatedUser.getName().trim().isEmpty()) {
            existingUser.setName(updatedUser.getName().trim());
        }

        // Update email if provided
        if (updatedUser.getEmail() != null) {
            String email = updatedUser.getEmail().trim();
            if (!email.isEmpty()) {
                // Check if email already exists for another user
                Optional<User> existingEmailUser = userRepository.findByEmail(email);
                if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
                }
                existingUser.setEmail(email);
            } else {
                existingUser.setEmail(null); // empty string normalized to null
            }
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {

        Optional<User> existingUser = userRepository.findById(id);

        if(existingUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found with ID "+id);

        }
        userRepository.deleteById(id);
    }
}
