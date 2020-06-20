package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

    UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getall")
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("getAllUsers");
        return userRepository.findAll(pageable);
    }

    @GetMapping("/getbyid/{userId}")
    public User getUserById(@PathVariable (value = "userId") Long userId) {
        log.info("getUserById: " + userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("getUserById: " + userId + " NoSuchElementException");
            return new NoSuchElementException("User with the current ID: " + userId + " was not found");
        });
    }
}
