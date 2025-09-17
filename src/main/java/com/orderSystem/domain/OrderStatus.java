package com.orderSystem.domain;

public enum OrderStatus {
    PROCESSING("처리중"),
    SHIPPING("배송중"),
    COMPLETED("완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}