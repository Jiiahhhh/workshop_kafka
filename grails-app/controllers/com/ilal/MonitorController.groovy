package com.ilal

class MonitorController {

    def kitchen() {
        def pendingOrders = Order.findAllByStatusInList([Order.OrderStatus.NEW, Order.OrderStatus.PROCESSING])
        [orders: pendingOrders]
    }

    def serving() {
        def readyOrders = Order.findAllByStatus(Order.OrderStatus.READY)
        [orders: readyOrders]
    }
}
