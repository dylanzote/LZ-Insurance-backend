package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.UnderwritingNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnderwritingNoteRepository extends JpaRepository<UnderwritingNoteEntity, String> {

    List<UnderwritingNoteEntity> findByQuoteIdOrderByCreatedAtAsc(String quoteId);
}
