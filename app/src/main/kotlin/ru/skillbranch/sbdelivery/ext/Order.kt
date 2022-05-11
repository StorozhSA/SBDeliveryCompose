package ru.skillbranch.sbdelivery.ext

import ru.skillbranch.sbdelivery.models.database.domains.EOrder
import ru.skillbranch.sbdelivery.models.database.domains.EOrderDish
import ru.skillbranch.sbdelivery.models.database.domains.EOrderStatus
import ru.skillbranch.sbdelivery.models.database.domains.EOrderWithDishes
import ru.skillbranch.sbdelivery.models.network.domains.ResOrder
import ru.skillbranch.sbdelivery.models.network.domains.ResOrdersStatusItem


public fun ResOrder.toOrderWithDishes(): EOrderWithDishes = EOrderWithDishes(
    order = EOrder(
        id = this.id,
        active = this.active,
        address = this.address,
        completed = this.completed,
        statusId = this.statusId,
        total = this.total,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    ),
    dishes = this.items.map {
        EOrderDish(
            orderId = this.id,
            dishId = it.dishId,
            amount = it.amount,
            price = it.price
        )
    }
)

public fun ResOrdersStatusItem.toEOrderStatus(): EOrderStatus = EOrderStatus(
    id = id,
    active = active,
    cancelable = cancelable,
    createdAt = createdAt,
    name = name,
    updatedAt = updatedAt
)
