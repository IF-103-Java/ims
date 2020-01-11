package com.ita.if103java.ims.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum EventName {
    LOGIN("Login", EventType.USER, false),
    SIGN_UP("Sign up", EventType.USER, false),
    LOGOUT("Logout", EventType.USER, false),
    PASSWORD_CHANGED("Password changed", EventType.USER, false),
    PROFILE_CHANGED("Profile info changed", EventType.USER, false),
    ACCOUNT_CREATED("Account created", EventType.ACCOUNT, false),
    ACCOUNT_EDITED("Account info edited", EventType.ACCOUNT, false),
    ACCOUNT_DELETED("Account deleted", EventType.ACCOUNT, false),
    WORKER_INVITED("New worker invited", EventType.ACCOUNT, false),
    WORKER_JOINED("Worker joined", EventType.ACCOUNT, true),
    ACCOUNT_UPGRADED("Account upgraded", EventType.ACCOUNT, true),
    WAREHOUSE_CREATED("Warehouse created", EventType.WAREHOUSE, true),
    WAREHOUSE_REMOVED("Warehouse removed", EventType.WAREHOUSE, true),
    WAREHOUSE_EDITED("Warehouse info edited", EventType.WAREHOUSE, false),
    ITEM_ENDED("Item is ended", EventType.WAREHOUSE, true),
    LOW_SPACE_IN_WAREHOUSE("Low space in capacity", EventType.WAREHOUSE, true),
    ITEM_CAME("Item came", EventType.TRANSACTION, true),
    ITEM_SHIPPED("Item shipped", EventType.TRANSACTION, true),
    ITEM_MOVED("Item moved", EventType.TRANSACTION, true),
    NEW_SUPPLIER("New supplier", EventType.PARTNER, true),
    NEW_CLIENT("New client", EventType.PARTNER, true),
    SUPPLIER_REMOVED("Supplier removed", EventType.PARTNER, true),
    CLIENT_REMOVED("Client removed", EventType.PARTNER, true),
    SUPPLIER_EDITED("Supplier info edited", EventType.PARTNER, false),
    CLIENT_EDITED("Client info edited", EventType.PARTNER, false);

    private String label;
    private EventType type;
    private boolean isNotification;

    private static final Map<String, EventName> lookup = new HashMap<String, EventName>();

    static {
        for (EventName d : EventName.values()) {
            lookup.put(d.getLabel(), d);
        }
    }

    EventName(String label, EventType type, boolean isNotification) {
        this.label = label;
        this.type = type;
        this.isNotification = isNotification;
    }

    public static Set<EventName> getValuesByType(EventType type) {
        Set<EventName> values = new HashSet<>();
        for (EventName name : EventName.values()) {
            if (name.getType() == type) {
                values.add(name);
            }
        }
        return values;
    }

    public EventType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public static Map<String, EventName> getLookup() {
        return lookup;
    }
}
