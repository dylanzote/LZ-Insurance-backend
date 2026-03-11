package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.UnderwritingNote;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class UnderwritingNoteResponse {

    private String id;
    private String quoteId;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;

    public static UnderwritingNoteResponse from(UnderwritingNote note) {
        UnderwritingNoteResponse response = new UnderwritingNoteResponse();
        BeanUtils.copyProperties(note, response);
        return response;
    }
}
