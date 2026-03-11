package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.UnderwritingNote;
import com.zote.policy.service.domain.models.data.AddUnderwritingNoteData;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder support for UnderwritingNote domain entity.
 */
@UtilityClass
public class UnderwritingNoteBuilderSupport {

    public UnderwritingNote buildFromAddData(AddUnderwritingNoteData data) {
        return UnderwritingNote.builder()
                .id(UUID.randomUUID().toString())
                .quoteId(data.getQuoteId())
                .note(data.getNote())
                .createdBy(data.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
