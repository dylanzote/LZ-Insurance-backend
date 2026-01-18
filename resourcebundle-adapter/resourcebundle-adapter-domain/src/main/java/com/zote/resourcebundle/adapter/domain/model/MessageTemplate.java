package com.zote.resourcebundle.adapter.domain.model;

import com.zote.common.utils.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageTemplate {
    private String id;
    private String name;
    private Language language;
    private String subject;
    private String body;

}
