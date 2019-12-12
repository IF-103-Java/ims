package com.ita.if103java.ims.entity;

import java.util.HashSet;
import java.util.Set;

public enum EventName {
    LOGIN("Login", EventType.USER),
    SIGN_UP("Sign up", EventType.USER),
    LOGOUT("Logout", EventType.USER),
    PASSWORD_CHANGED("Password changed", EventType.USER),
    PROFILE_CHANGED("Profile info changed", EventType.USER),
    ORG_CREATED("Organization created", EventType.ORGANIZATION),
    ORG_EDITED("Organization info edited", EventType.ORGANIZATION),
    WORKER_INVITED("New worker invited", EventType.ORGANIZATION),
    WORKER_JOINED("Worker joined", EventType.ORGANIZATION),
    ORG_UPGRADED("Organization upgraded", EventType.ORGANIZATION),
    WAREHOUSE_CREATED("Warehouse created", EventType.WAREHOUSE),
    WAREHOUSE_REMOVED("Warehouse removed", EventType.WAREHOUSE),
    WAREHOUSE_EDITED("Warehouse info edited", EventType.WAREHOUSE),
    ITEM_ENDED("Item is ended", EventType.WAREHOUSE),
    LOW_SPACE_IN_WAREHOUSE("Low space in capacity", EventType.WAREHOUSE),
    ITEM_CAME("Item came", EventType.TRANSACTION),
    ITEM_SHIPPED("Item shipped", EventType.TRANSACTION),
    ITEM_MOVED("Item moved", EventType.TRANSACTION),
    NEW_SUPPLIER("New supplier", EventType.PARTNER),
    NEW_CLIENT("New client", EventType.PARTNER),
    SUPPLIER_REMOVED("Supplier removed", EventType.PARTNER),
    CLIENT_REMOVED("Client removed", EventType.PARTNER),
    SUPPLIER_EDITED("Supplier info edited", EventType.PARTNER),
    CLIENT_EDITED("Client info edited", EventType.PARTNER);

    private String label;
    private EventType type;

    EventName(String label, EventType type) {
        this.label = label;
        this.type = type;
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
}
