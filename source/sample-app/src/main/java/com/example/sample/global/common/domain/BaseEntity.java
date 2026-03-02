package com.example.sample.global.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * BaseEntity
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @LastModifiedDate/**
     * CurrentUserId
     *
     * @author : hhh
     * @version 1.0
     * @date : 1/31/26
     */
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
