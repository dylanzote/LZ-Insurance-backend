package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.UnderwritingNote;

import java.util.List;

public interface UnderwritingNoteRepositoryPort {

    UnderwritingNote save(UnderwritingNote note);

    List<UnderwritingNote> findByQuoteId(String quoteId);
}
