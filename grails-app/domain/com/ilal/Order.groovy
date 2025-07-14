package com.ilal

class Order {
    BigDecimal totalPrice = 0.0
    OrderStatus status = OrderStatus.NEW
    Date dateCreated

    static hasMany = [items: OrderItem]

    static constraints = {
        totalPrice min: 0.0G
    }

    static mapping = {
        table 'orders'
    }

    enum OrderStatus {
        NEW,            // Order just placed
        PROCESSING,     // Being prepared by Kitchen/Barista
        READY,           // Ready at the serving counter
        DELIVERED       // Delivered to the customer
    }
}
