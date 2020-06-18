package com.example.demo.repository;

import com.example.demo.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Page<Tag> findByPosts_Id(Long postId, Pageable pageable);

    Optional<Tag> findByIdAndPosts_Id(Long id, Long postId);
}
