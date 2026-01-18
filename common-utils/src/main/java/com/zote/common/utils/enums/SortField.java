package com.zote.common.utils.enums;

import lombok.Getter;

@Getter
public enum SortField {

    CREATED_ON("createdAt");

    private final String fieldName;

    SortField(String fieldName) {
        this.fieldName = fieldName;
    }
}
