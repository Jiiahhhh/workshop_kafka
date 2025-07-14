package com.ilal

class OrderController {

    KafkaProducerService kafkaProducerService
    OrderCreationService orderCreationService

    def index() {
        // Action to display GSP for create order
        [menuItems: MenuItem.list()]
    }

    def createOrder() {
        def orderedItems = params.findAll { it.key.startsWith('item.') && it.value.isInteger() && it.value.toInteger() > 0 }

        if (orderedItems.isEmpty()) {
            flash.message = "Error: No items were ordered"
            redirect(action: 'index')
            return
        }

        // 1. Call service for save it to DB
        // When the line is finished, transaction will be commited
        Order newOrder = orderCreationService.createOrderInDb(orderedItems)

        // 2. After data already in DB, we can sand it to Kafka
        if (newOrder) {
            List<Map> eventItems = newOrder.items.collect { orderItem ->
                [
                        menuItemId: orderItem.menuItem.id,
                        name: orderItem.menuItem.name,
                        category: orderItem.menuItem.category.toString(),
                        quantity: orderItem.quantity
                ]
            }

            def event = [
                    orderId: newOrder.id,
                    totalPrice: newOrder.totalPrice,
                    orderTime: newOrder.dateCreated.toInstant().toString(),
                    items: eventItems
            ]
            kafkaProducerService.send("new-orders", event)

            flash.message = "Order #${newOrder.id} successfully placed and sent to the kitchen!"
        } else {
            flash.message = "Error: Failed to create order."
        }
        redirect(action: 'index')
    }
}
