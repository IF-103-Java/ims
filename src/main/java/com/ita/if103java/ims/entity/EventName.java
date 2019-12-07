package com.ita.if103java.ims.entity;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public enum EventName {
    LOGIN("Login", EventType.USER),
    SIGNUP("Sign up", EventType.USER),
    LOGOUT("Logout", EventType.USER),
    PASSWORDCHANGED("Password changed", EventType.USER),
    PROFILECHANGED("Profile info changed", EventType.USER),
    ORGCREADTED("Organization created", EventType.ORGANIZATION),
    ORGEDITED("Organization info edited", EventType.ORGANIZATION),
    WORKERINVITED("New worker invited", EventType.ORGANIZATION),
    WORKERJOINED("Worker joined", EventType.ORGANIZATION),
    ORGUPGRADED("Organization upgraded", EventType.ORGANIZATION),
    WAREHOUSECREATED("Warehouse created", EventType.WAREHOUSE),
    WAREHOUSEREMOVED("Warehouse removed", EventType.WAREHOUSE),
    WAREHOUSEDITED("Warehouse info edited", EventType.WAREHOUSE),
    ITEMENDED("Item is ended", EventType.WAREHOUSE),
    LOWSPACEINWAREHOUSE("Low space in capacity", EventType.WAREHOUSE),
    ITEMCAME("Item came", EventType.TRANSACTION),
    ITEMSHIPPED("Item shipped", EventType.TRANSACTION),
    ITEMMOVED("Item moved", EventType.TRANSACTION),
    NEWSUPPLIER("New supplier", EventType.PARTNER),
    NEWCLIENT("New client", EventType.PARTNER),
    SUPPLIERREMOVED("Supplier removed", EventType.PARTNER),
    CLIENTREMOVED("Client removed", EventType.PARTNER),
    SUPPLIEREDITED("Supplier info edited", EventType.PARTNER),
    CLIENTEDITED("Client info edited", EventType.PARTNER);

    private String label;
    private EventType type;

    EventName(String label, EventType type) {
        this.label = label;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public static Set<EventName> getValuesByType(EventType type){
        Set<EventName> values = new HashSet<>(7);
        for (EventName name : EventName.values()){
            if (name.getType() == type){
                values.add(name);
            }
        }
        return values;
    }
}
