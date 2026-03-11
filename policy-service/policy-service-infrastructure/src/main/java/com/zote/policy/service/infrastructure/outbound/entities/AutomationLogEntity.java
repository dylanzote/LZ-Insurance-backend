package com.zote.policy.service.infrastructure.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "automation_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationLogEntity {

    @Id
    private String id;

    @Column(name = "job_type", nullable = false, length = 64)
    private String jobType;

    @Column(name = "status", nullable = false, length = 24)
    private String status;

    @Column(name = "items_processed")
    private int itemsProcessed;

    @Column(name = "items_succeeded")
    private int itemsSucceeded;

    @Column(name = "items_failed")
    private int itemsFailed;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
