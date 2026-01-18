package com.zote.kafka.adapter.event;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorCodeEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String userId;
    private String email;
    private String firstName;
    private Language language;
    private String code;
    private TwoFacMethod method;
    private int expiresInMinutes;

    @Override
    public String getEventType() {
        return EventType.TWO_FACTOR_CODE_SENT.getCode();
    }
}

