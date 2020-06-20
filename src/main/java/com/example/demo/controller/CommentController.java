package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/comment")
@Log4j2
public class CommentController {
    CommentRepository commentRepository;
    PostRepository postRepository;

    CommentController(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/getall")
    public Page<Comment> getAllComments(Pageable pageable) {
        log.info("getAllComments");
        return commentRepository.findAll(pageable);
    }

    @GetMapping("/getbyid/{commentId}")
    public Comment getCommentById(@PathVariable (value = "commentId") Long commentId) {
        log.info("getCommentById: " + commentId);
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("getCommentById: " + commentId + " NoSuchElementException");
            return new NoSuchElementException("Comment with the current ID: " + commentId + " is not found");
        });
    }

    @GetMapping("posts/{postId}")
    public Page<Comment> getAllCommentsByPostId(@PathVariable (value = "postId") Long postId,
                                                Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @PostMapping("posts/{postId}")
    public Comment createComment(@PathVariable (value = "postId") Long postId,
                                 @Valid @RequestBody Comment comment) {
        return postRepository.findById(postId).map(post -> {
            comment.setPost(post);
            return commentRepository.save(comment);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public Comment updateComment(@PathVariable (value = "postId") Long postId,
                                 @PathVariable (value = "commentId") Long commentId,
                                 @Valid @RequestBody Comment commentRequest) {
        if(!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("PostId " + postId + " not found");
        }
        return commentRepository.findById(commentId).map(comment -> {
            comment.setText(commentRequest.getText());
            return commentRepository.save(comment);
        }).orElseThrow(() -> new ResourceNotFoundException("CommentId " + commentId + "not found"));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable (value = "postId") Long postId,
                                           @PathVariable (value = "commentId") Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId).map(comment -> {
            commentRepository.delete(comment);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId + " and postId " + postId));
    }
}
