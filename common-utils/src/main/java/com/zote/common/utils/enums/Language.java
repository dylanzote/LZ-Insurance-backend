package com.zote.common.utils.enums;

public enum Language {
    EN("en", "English"),
    FR("fr", "Français");

    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Language fromCode(String code) {
        if (code == null) {
            return EN; // Default to English
        }
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return EN; // Default to English
    }
}