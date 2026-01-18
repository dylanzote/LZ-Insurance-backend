package com.zote.notification.service.api.controller;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.request.*;
import com.zote.notification.service.api.response.TemplateRenderResponse;
import com.zote.notification.service.api.response.TemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Template Management API")
@RequestMapping("/notification/templates")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface TemplateApi {

    @Operation(summary = "Create a template")
    @PostMapping
    TemplateResponse createTemplate(
            @Valid @RequestBody CreateTemplateRequest request);

    @Operation(summary = "Update template")
    @PutMapping("/{templateId}")
    TemplateResponse updateTemplate(
            @PathVariable String templateId,
            @Valid @RequestBody UpdateTemplateRequest request);

    @Operation(summary = "Get template by ID")
    @GetMapping("/{templateId}")
    TemplateResponse getTemplate(@PathVariable("templateId") String templateId);

    @Operation(summary = "Search templates")
    @GetMapping
    PageResponse searchTemplates(
            @Parameter(description = "Filter by notification channel", schema = @Schema(implementation = NotificationChannel.class))
            @RequestParam(name = "channel") NotificationChannel channel,
            @RequestParam(name = "isActive") Boolean isActive,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size);

    @Operation(summary = "Add template translation")
    @PostMapping("/{templateId}/translations/{locale}")
    TemplateResponse addTranslation(
            @PathVariable("templateId") String templateId,
            @PathVariable("locale") String locale,
            @Valid @RequestBody AddTranslationRequest request);

    @Operation(summary = "Update template translation")
    @PutMapping("/{templateId}/translations/{locale}")
    TemplateResponse updateTranslation(
            @PathVariable("templateId") String templateId,
            @PathVariable("locale") String locale,
            @Valid @RequestBody UpdateTranslationRequest request);

    @Operation(summary = "Render template preview")
    @PostMapping("/{templateId}/render")
    TemplateRenderResponse renderTemplate(
            @PathVariable("templateId") String templateId,
            @Valid @RequestBody RenderTemplateRequest request);

    @Operation(summary = "Delete template")
    @DeleteMapping("/{templateId}")
    void deleteTemplate(@PathVariable("templateId") String templateId);
}
