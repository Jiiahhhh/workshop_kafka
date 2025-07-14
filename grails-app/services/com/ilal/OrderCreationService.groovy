package com.ilal

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@Slf4j
class OrderCreationService {

    @Transactional
    Order createOrderInDb(Map<String, String> orderedItems) {
        if (orderedItems.isEmpty()) {
            return null
        }

        Order newOrder = new Order().save(flush: true, failOnError: true)
        BigDecimal finalTotalPrice = 0.0G

        orderedItems.each { key, quantityStr ->
            Long menuItemId = key.split('\\.')[1].toLong()
            int quantity = quantityStr.toInteger()
            MenuItem menuItem = MenuItem.get(menuItemId)

            if (menuItem) {
                OrderItem orderItem = new OrderItem(order: newOrder, menuItem: menuItem, quantity: quantity).save(failOnError: true)
                newOrder.addToItems(orderItem)
                finalTotalPrice += (menuItem.price * quantity)
            }
        }

        newOrder.totalPrice = finalTotalPrice
        newOrder.save(flush: true, failOnError: true)

        return newOrder
    }
}
