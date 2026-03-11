package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.policy.service.domain.models.UnderwritingNote;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "underwriting_note")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnderwritingNoteEntity {

    @Id
    private String id;

    @Column(name = "quote_id", nullable = false, length = 64)
    private String quoteId;

    @Column(name = "note", nullable = false, length = 2048)
    private String note;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    public static UnderwritingNoteEntity toEntity(UnderwritingNote model) {
        return UnderwritingNoteEntity.builder()
                .id(model.getId())
                .quoteId(model.getQuoteId())
                .note(model.getNote())
                .createdBy(model.getCreatedBy())
                .createdAt(model.getCreatedAt())
                .build();
    }

    public UnderwritingNote toDto() {
        return UnderwritingNote.builder()
                .id(id)
                .quoteId(quoteId)
                .note(note)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .build();
    }
}
