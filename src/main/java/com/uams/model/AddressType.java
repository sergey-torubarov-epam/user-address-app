package com.uams.model;

public enum AddressType {
    HOME("Home"),
    WORK("Work"),
    TEMPORARY("Temporary"),
    PERMANENT("Permanent");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}