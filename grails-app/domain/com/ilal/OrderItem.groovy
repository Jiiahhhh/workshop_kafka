package com.ilal

class OrderItem {
    int quantity
    ItemStatus status = ItemStatus.PENDING
    static belongsTo = [order: Order, menuItem: MenuItem]

    static constraints = {
        quantity min: 1
    }

    enum ItemStatus {
        PENDING,
        COMPLETED
    }
}
