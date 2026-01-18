package com.zote.notification.service.domain.model;

import com.zote.notification.service.domain.model.enums.VariableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVariable {
    private VariableType type;
    private Boolean required;
    private String defaultValue;
    private String description;
    private Integer maxLength;
    private String pattern;
    private Set<String> enumValues;
}
