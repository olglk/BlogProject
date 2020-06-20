package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import lombok.extern.log4j.Log4j2;
import com.example.demo.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
//import com.example.jpa.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/post")
@Log4j2
public class PostController {

    PostRepository postRepository;
    TagRepository tagRepository;

    PostController(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/getall")
    public Page<Post> getAllPosts(Pageable pageable) {
        log.info("getAllPosts");
        return postRepository.findAll(pageable);
    }

    @GetMapping("/getbyid/{postId}")
    public Post getPostById(@PathVariable (value = "postId") Long postId) {
        log.info("getPostById: " + postId);
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("getPostById: " + postId + " NoSuchElementException");
            return new NoSuchElementException("Post with the current ID: " + postId + " is not found");
        });
    }

    @PostMapping
    public Post createPost(@Valid @RequestBody Post post) {
        return postRepository.save(post);
    }

    @PutMapping("{postId}")
    public Post updatePost(@PathVariable (value = "postId") Long postId, @Valid @RequestBody Post postRequest) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(postRequest.getTitle());
            post.setDescription(postRequest.getDescription());
            post.setContent(postRequest.getContent());
            return postRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<?> deletePost(@PathVariable (value = "postId") Long postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    //Many to Many Relation with Tag class

    @GetMapping("tags/{tagId}")
    public Page<Post> getAllPostsByTagId(@PathVariable (value = "tagId") Long tagId,
                                         Pageable pageable) {
        return postRepository.findByTags_Id(tagId, pageable);
    }

    @PostMapping("/tags/{tagId")
    public Post createPostWithTagId(@PathVariable (value = "tagId") Long tagId,
                                    @Valid @RequestBody Post post) {
        return tagRepository.findById(tagId).map(tag -> {
            post.addTag(tag);
            return postRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("TagId" + tagId + " not found"));
    }

    @PutMapping("/posts/{postId}/tags/{tagId}")
    public Post updatePostWithTag(@PathVariable (value = "postId") Long postId,
                                  @PathVariable (value = "tagId") Long tagId,
                                  @Valid @RequestBody Post postRequest) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("TagId " + tagId + " not found");
        }
        return postRepository.findById(postId).map(post -> {
            post.setTitle(postRequest.getTitle());
            post.setDescription(postRequest.getDescription());
            post.setContent(postRequest.getContent());
            return postRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @DeleteMapping("/posts/{postId}/tags/{tagId}")
    public ResponseEntity<?> deletePostWithTag(@PathVariable (value = "postId") Long postId,
                                               @PathVariable (value = "tagId") Long tagId) {
        return postRepository.findByIdAndTags_Id(postId, tagId).map(post -> {
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }
}

