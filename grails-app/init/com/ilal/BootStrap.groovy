package com.ilal

import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {

    BaristaService baristaService
    KitchenService kitchenService
    WaiterService waiterService

    def init = { servletContext ->
        log.info("Bootstrap: Forcing initialization of customer service...")
        // Create data only if the data not exists
        if (MenuItem.count == 0) {
            println("Creating sample menu items...")
            new MenuItem(name: "Kopi Susu Gula Aren", price: 18000, category: MenuItem.Category.DRINK).save()
            new MenuItem(name: "Americano", price: 15000, category: MenuItem.Category.DRINK).save()
            new MenuItem(name: "Pisang Epe Coklat Keju", price: 22000, category: MenuItem.Category.FOOD).save()
            new MenuItem(name: "Indomie Goreng Special", price: 20000, category: MenuItem.Category.FOOD).save()
            println("Sample menu items created.")
        }
    }
    def destroy = {
    }
}