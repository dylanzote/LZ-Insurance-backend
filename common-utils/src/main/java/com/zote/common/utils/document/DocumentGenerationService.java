package com.zote.common.utils.document;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.zote.common.utils.files.MinioObjectStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Generic, cross-service document generation.
 * Renders HTML + data → final HTML, converts to PDF, uploads to MinIO.
 * Any microservice can use this by providing template, data, and optional metadata.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentGenerationService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final MinioObjectStorage minioObjectStorage;
    private final ResourceLoader resourceLoader;

    /**
     * Generate a document from request.
     *
     * @param request template (string or path), data, metadata
     * @return documentUrl, documentId, documentType, createdAt
     */
    public DocumentGenerationResult generate(DocumentGenerationRequest request) {
        String html = resolveAndRenderHtml(request);
        byte[] pdfBytes = htmlToPdf(html);

        DocumentMetadata meta = request.getMetadata() != null ? request.getMetadata() : DocumentMetadata.builder().build();
        String serviceName = meta.getServiceName() != null ? meta.getServiceName() : "documents";
        String docType = meta.getDocumentType() != null ? meta.getDocumentType() : "document";
        String entityId = meta.getEntityId() != null ? meta.getEntityId() : UUID.randomUUID().toString();

        String objectName = minioObjectStorage.getDocumentObjectName(serviceName, docType, entityId, ".pdf");
        minioObjectStorage.uploadBytes(pdfBytes, objectName, CONTENT_TYPE_PDF);
        String documentUrl = minioObjectStorage.getPresignedUrl(objectName);
        String documentId = UUID.randomUUID().toString();

        log.info("Generated document: type={}, entityId={}, objectName={}", docType, entityId, objectName);

        return DocumentGenerationResult.builder()
                .documentId(documentId)
                .documentUrl(documentUrl)
                .documentType(docType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String resolveAndRenderHtml(DocumentGenerationRequest request) {
        String html;
        if (request.getHtmlTemplate() != null && !request.getHtmlTemplate().isEmpty()) {
            html = request.getHtmlTemplate();
        } else if (request.getTemplatePath() != null && !request.getTemplatePath().isEmpty()) {
            html = loadTemplateFromClasspath(request.getTemplatePath());
        } else {
            throw new IllegalArgumentException("Either htmlTemplate or templatePath must be provided");
        }
        return renderTemplate(html, request.getData());
    }

    private String loadTemplateFromClasspath(String path) {
        String resourcePath = path.startsWith("classpath:") ? path : "classpath:" + path;
        Resource resource = resourceLoader.getResource(resourcePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Template not found: " + resourcePath);
        }
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template: " + path, e);
        }
    }

    /**
     * Simple {{variableName}} replacement.
     */
    private String renderTemplate(String template, Map<String, Object> data) {
        if (template == null) return "";
        if (data == null || data.isEmpty()) return template;

        String result = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    private byte[] htmlToPdf(String html) {
        try {
            String xhtml = toXhtml(html);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(xhtml, null);
            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to convert HTML to PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Wrap HTML in basic XHTML structure if needed for openhtmltopdf.
     */
    private String toXhtml(String html) {
        String trimmed = html != null ? html.trim() : "";
        if (trimmed.isEmpty()) {
            return "<html><head></head><body></body></html>";
        }
        if (!trimmed.toLowerCase().startsWith("<!doctype") && !trimmed.toLowerCase().startsWith("<html")) {
            return "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta charset=\"UTF-8\"/></head><body>" + trimmed + "</body></html>";
        }
        return trimmed;
    }
}
