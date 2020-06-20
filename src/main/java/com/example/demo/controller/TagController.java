package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Tag;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tag")
@Log4j2
public class TagController {

    TagRepository tagRepository;
    PostRepository postRepository;

    TagController(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/getall")
    public Page<Tag> getAllTags(Pageable pageable) {
        log.info("getAllTags");
        return tagRepository.findAll(pageable);
    }

    @GetMapping("/getbyid/{tagId}")
    public Tag getTagById(@PathVariable (value = "tagId") Long tagId) {
        log.info("getTagById: " + tagId);
        return tagRepository.findById(tagId).orElseThrow(() -> {
            log.error("getTagById: " + tagId + " oSuchElementException");
            return new NoSuchElementException("Tag with the current ID: " + tagId + " was nor found");
        });
    }

    @GetMapping("/posts/{postId}")
    public Page<Tag> getAllTagsByPostId(@PathVariable (value = "postId") Long postId,
                                        Pageable pageable) {
        return tagRepository.findByPosts_Id(postId, pageable);
    }

    @PostMapping("/posts/{postId}")
    public Tag createTagWithPostId(@PathVariable (value = "postId") Long postId,
                         @Valid @RequestBody Tag tag) {
        return postRepository.findById(postId).map(post -> {
            tag.addPost(post);
            return tagRepository.save(tag);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @PutMapping("/posts/{postId}/tags/{tagId}")
    public Tag updateTag(@PathVariable (value = "postId") Long postId,
                         @PathVariable (value = "tagId") Long tagId,
                         @Valid @RequestBody Tag tagRequest) {
        if(!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("PostId " + postId + " not found");
        }
        return tagRepository.findById(tagId).map(tag -> {
            tag.setName(tagRequest.getName());
            return tagRepository.save(tag);
        }).orElseThrow(() -> new ResourceNotFoundException("TagId " + tagId + " not found"));
    }

    @DeleteMapping("/posts/{postId}/tags/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable (value = "postId") Long postId,
                                       @PathVariable (value = "tagId") Long tagId) {
        return tagRepository.findByIdAndPosts_Id(tagId, postId).map(tag -> {
            tagRepository.delete(tag);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("TagId " + tagId + " not found"));
    }

}
