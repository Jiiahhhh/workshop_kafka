package com.ilal

class OrderItem {
    int quantity
    ItemStatus status = ItemStatus.PENDING

    // many to one (One 'OrderItem' belongs to one 'Order' refers to One 'MenuItem')
    static belongsTo = [order: Order, menuItem: MenuItem]

    static constraints = {
        quantity min: 1
    }

    enum ItemStatus {
        PENDING,    // Waiting or in progress
        COMPLETED   // Item has been completed
    }
}
