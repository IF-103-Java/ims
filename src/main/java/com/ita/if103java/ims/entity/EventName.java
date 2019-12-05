package com.ita.if103java.ims.entity;

public enum EventName {
    login(EventType.USER),
    signUp(EventType.USER),
    logout(EventType.USER),
    passwordChanged(EventType.USER),
    profileInfoChanged(EventType.USER),
    orgCreated(EventType.ORGANIZATION),
    orgEdited(EventType.ORGANIZATION),
    workerInvited(EventType.ORGANIZATION),
    workerJoined(EventType.ORGANIZATION),
    orgUpgraded(EventType.ORGANIZATION),
    warehouseCreated(EventType.WAREHOUSE),
    warehouseRemoved(EventType.WAREHOUSE),
    warehouseEdited(EventType.WAREHOUSE),
    itemsIsEnded(EventType.WAREHOUSE),
    lowSpaceInWarehouse(EventType.WAREHOUSE),
    itemCame(EventType.TRANSACTION),
    itemShipped(EventType.TRANSACTION),
    itemMoved(EventType.TRANSACTION),
    newSupplier(EventType.PARTNER),
    newClient(EventType.PARTNER),
    supplierRemoved(EventType.PARTNER),
    clientRemoved(EventType.PARTNER),
    supplierEdited(EventType.PARTNER),
    clientEdited(EventType.PARTNER);

    private EventType type;

    EventName(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
