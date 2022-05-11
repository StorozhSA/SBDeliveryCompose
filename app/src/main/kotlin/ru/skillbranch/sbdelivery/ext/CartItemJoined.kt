package ru.skillbranch.sbdelivery.ext

import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.network.domains.CartItem

public fun CartItemJoined.asECartItem(): ECartItem =
    ECartItem(id = id, amount = amount, price = price)

public fun CartItemJoined.asCartItem(): CartItem =
    CartItem(id = id, amount = amount, price = price)
