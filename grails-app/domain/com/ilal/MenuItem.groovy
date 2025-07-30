package com.ilal

class MenuItem {
    String name
    BigDecimal price // I choose BigDecimal to price to avoid rounding error.
    Category category // To save item category, the value takes from Category enum

    static constraints = {
        name blank: false, unique: true
        price min: 0.0G // G = BigDecimal value
        category nullable: false
    }

    // Enum is special data type to a list of value who cannot be changed.
    // It is more secure than String, to avoid type such as "FOOD" vs "food"
    enum Category {
        DRINK,
        FOOD
    }
}
