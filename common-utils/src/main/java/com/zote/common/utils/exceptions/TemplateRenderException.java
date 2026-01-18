package com.zote.common.utils.exceptions;

public class TemplateRenderException extends RuntimeException {
    public TemplateRenderException(String message) {
        super(message);
    }

    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
