package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
public abstract class AuditModel implements Serializable {

    @Column(columnDefinition = "DATE", name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;

    @Column(columnDefinition = "DATE", name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDate updatedAt;
}
