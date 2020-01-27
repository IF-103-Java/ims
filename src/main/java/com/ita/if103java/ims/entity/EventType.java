package com.ita.if103java.ims.entity;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    USER("User"),
    ACCOUNT("Account"),
    WAREHOUSE("Warehouse"),
    TRANSACTION("Transaction"),
    PARTNER("Partner");

    private static final Map<String, EventType> lookup = new HashMap<String, EventType>();

    static {
        for (EventType d : EventType.values()) {
            lookup.put(d.getLabel(), d);
        }
    }

    private String label;

    EventType(String label) {
        this.label = label;
    }

    public static Map<String, EventType> getLookup() {
        return lookup;
    }

    public String getLabel() {
        return label;
    }
}
