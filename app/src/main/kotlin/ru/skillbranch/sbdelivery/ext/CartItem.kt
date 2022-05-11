package ru.skillbranch.sbdelivery.ext

import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.network.domains.CartItem

public fun CartItem.asECartItem(): ECartItem = ECartItem(id = id, amount = amount, price = price)
