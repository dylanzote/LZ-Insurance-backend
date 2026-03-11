package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.UnderwritingNote;
import com.zote.policy.service.domain.ports.outbound.UnderwritingNoteRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.UnderwritingNoteEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.UnderwritingNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnderwritingNoteRepositoryPortImpl implements UnderwritingNoteRepositoryPort {

    private final UnderwritingNoteRepository underwritingNoteRepository;

    @Override
    public UnderwritingNote save(UnderwritingNote note) {
        log.info("Saving underwriting note for quote {}", note.getQuoteId());
        return underwritingNoteRepository.save(UnderwritingNoteEntity.toEntity(note)).toDto();
    }

    @Override
    public List<UnderwritingNote> findByQuoteId(String quoteId) {
        log.info("Finding underwriting notes for quote {}", quoteId);
        return underwritingNoteRepository.findByQuoteIdOrderByCreatedAtAsc(quoteId)
                .stream()
                .map(UnderwritingNoteEntity::toDto)
                .toList();
    }
}
