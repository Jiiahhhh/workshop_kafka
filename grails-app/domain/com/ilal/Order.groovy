package com.ilal

class Order {
    BigDecimal totalPrice = 0.0
    OrderStatus status = OrderStatus.NEW // The current status order
    Date dateCreated // Date when order created, Grails automatically fill it when it saved.

    // One to many (One 'Order' can have many 'OrderItem')
    // Grails will create a set name 'items' in every Order object.
    static hasMany = [items: OrderItem]

    static constraints = {
        totalPrice min: 0.0G // G = BigDecimal value
    }

    static mapping = {
        // Change table's name to 'orders' to avoid conflict from ORDER command (SQL reserved keyword).
        table 'orders'
    }

    // Enum is special data type to a list of value who cannot be changed.
    enum OrderStatus {
        NEW,            // Order just placed
        PROCESSING,     // Being prepared by Kitchen/Barista
        READY,           // Ready at the serving counter
        DELIVERED       // Delivered to the customer
    }
}
