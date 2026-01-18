package com.zote.common.utils.models;


public class Permissions {

    public static final String IS_USER = "USER";

    public static final String IS_AGENT = "AGENT";

    public static final String IS_ADMIN = "ADMIN";

    public static final String IS_CUSTOMER = "CUSTOMER";

    public static final String[] ALL_PERMITTED = {IS_ADMIN, IS_CUSTOMER, IS_USER, IS_AGENT};

    private Permissions() {
    }
}
