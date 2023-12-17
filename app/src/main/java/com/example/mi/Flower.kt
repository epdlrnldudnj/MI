package com.example.mi

data class Flowers(val imageId: Int, val price: Int, var status: Int)

val price1 = 50
val price2 = 100
private var flowersClass = listOf(
    ShoppingPage.Flowers(R.drawable.flower_1, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_2, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_3, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_4, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_5, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_6, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_7, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_8, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_9, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_10, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_11, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_12, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_13, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_14, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_15, price1, 0),
    ShoppingPage.Flowers(R.drawable.flower_16, price2, 0),
    ShoppingPage.Flowers(R.drawable.flower_17, price2, 0),
    ShoppingPage.Flowers(R.drawable.flower_18, price2, 0),
    ShoppingPage.Flowers(R.drawable.flower_19, price2, 0),
    ShoppingPage.Flowers(R.drawable.flower_20, price2, 0),
)
