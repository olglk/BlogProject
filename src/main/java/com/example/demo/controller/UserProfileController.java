package com.example.demo.controller;

import com.example.demo.model.Gender;
import com.example.demo.model.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/userprofile")
@Log4j2
public class UserProfileController {

    UserProfileRepository userProfileRepository;

    UserProfileController(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }


    @GetMapping("/getall")
    public Page<UserProfile> getAllUserProfiles(Pageable pageable) {
        log.info("getAllUserProfiles");
        return userProfileRepository.findAll(pageable);
    }

    @GetMapping("/getbyid/{userProfileId}")
    public UserProfile getUserProfileById(@PathVariable(value = "userProfileId") Long userProfileId) {
        log.info("getUserProfileById: " + userProfileId);
        return userProfileRepository.findById(userProfileId).orElseThrow(() -> {
            log.error("getUserProfileById: " + userProfileId + " NoSuchElementException");
            return new NoSuchElementException("UserProfile with the current ID: " + userProfileId + " was not found");
        });
    }

    @PostMapping
    public UserProfile createUserProfile(@Valid @RequestBody UserProfile userProfile) {
        switch (userProfile.getGender()) {
            case MALE: userProfile.setGender(Gender.MALE); break;
            case FEMALE: userProfile.setGender(Gender.FEMALE); break;
        }
        return userProfileRepository.save(userProfile);
    }
}
