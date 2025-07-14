package com.ilal

class MenuItem {
    String name
    BigDecimal price
    Category category

    static constraints = {
        name blank: false, unique: true
        price min: 0.0G
        category nullable: false
    }

    enum Category {
        DRINK, FOOD
    }
}
