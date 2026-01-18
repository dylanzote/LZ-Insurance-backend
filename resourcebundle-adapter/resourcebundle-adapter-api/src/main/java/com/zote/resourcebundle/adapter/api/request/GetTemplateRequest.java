package com.zote.resourcebundle.adapter.api.request;

import com.zote.common.utils.enums.Language;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;

@Hidden
public record GetTemplateRequest(
        @NotNull
        Language language,

        @NotNull
        String templateName
) {
}
