package com.ilal

// @Transactional to database management, @Slf4j to logging.
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@Slf4j
class OrderCreationService {

    @Transactional
    // Method received data order from controller and will return ready Order object
    Order createOrderInDb(Map<String, String> orderedItems) {
        if (orderedItems.isEmpty()) {
            return null
        }

        // - new Order(): Create new Order object in memory
        // - .save(...): Save to database
        // - flush: true: Force Grails/Hibernate to send INSERT SQL command to db immediately. It doesn't waiting to the end of transaction.
        // - failOnError: true: if there is an error while save (DB Connection failed), return exception, not only return null. It will trigger rollback from @Transactional.
        Order newOrder = new Order().save(flush: true, failOnError: true)
        BigDecimal finalTotalPrice = 0.0G // G = BigDecimal

        orderedItems.each { key, quantityStr ->
            // Split key 'item.1' to get ID '1'.
            Long menuItemId = key.split('\\.')[1].toLong()
            // Convert quantity from String to Integer
            int quantity = quantityStr.toInteger()
            // Retrieve menuItem object from database based of its ID
            MenuItem menuItem = MenuItem.get(menuItemId)

            if (menuItem) {
                // Creae new OrderItem object
                // Connect 'newOrder' and 'menuItem' created before.
                // Save to db
                OrderItem orderItem = new OrderItem(order: newOrder, menuItem: menuItem, quantity: quantity).save(failOnError: true)

                // addToItems method automatically created by Grails because I have define 'hasMany = [items: ...]' in Order.groovy
                newOrder.addToItems(orderItem)

                // Calculate totalPrice
                finalTotalPrice += (menuItem.price * quantity)
            }
        }

        newOrder.totalPrice = finalTotalPrice
        newOrder.save(flush: true, failOnError: true)

        // Return the completed Order object and has saved in DB to its caller (OrderController)
        return newOrder
    }
}
