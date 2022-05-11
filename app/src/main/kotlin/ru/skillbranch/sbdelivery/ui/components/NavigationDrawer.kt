package ru.skillbranch.sbdelivery.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.domain.UiDrawerMenuItem
import ru.skillbranch.sbdelivery.ext.base
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature

public fun List<UiDrawerMenuItem>.existsRoute(route: String): Boolean =
    this.count { mi -> mi.route.contains(route) } > 0

@JvmInline
public value class CartCount(public val count: Int)


@SuppressLint("UnrememberedMutableState")
@Composable
public fun NavigationDrawer(
    currentRoute: String,
    modifier: Modifier = Modifier,
    menuItems: List<UiDrawerMenuItem> = emptyList(),
    notificationCount: Int = 0,
    cartCount: CartCount = CartCount(0),
    onLogout: () -> Unit,
    onSelect: (String) -> Unit
) {
    var isSelected by remember { mutableStateOf(-1) }
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val firstName = mutableStateOf(sharedPref.getString("userFirstName", "").toString())
    val lastName = mutableStateOf(sharedPref.getString("userLastName", "").toString())
    val email = mutableStateOf(sharedPref.getString("userEmail", "").toString())

    Column(modifier = modifier.background(color = MaterialTheme.colors.surface)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.7f)
                .background(color = MaterialTheme.colors.background),
            contentAlignment = BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_drawer),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${firstName.value} ${lastName.value}",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = email.value,
                    style = MaterialTheme.typography.body1
                )
            }

            IconButton(
                onClick = { onLogout() },
                modifier = Modifier.offset(x = (-12).dp, y = (-12).dp)
            ) {
                Icon(
                    tint = MaterialTheme.colors.onBackground,
                    painter = painterResource(id = R.drawable.ic_baseline_exit_to_app_24),
                    contentDescription = "Logout"
                )
            }
        }


        menuItems.forEachIndexed { index, menuItem ->
            if (menuItem.show) {

                if (menuItems.existsRoute(currentRoute) && menuItem.route.contains(currentRoute.base())) {
                    isSelected = index
                }

                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .height(44.dp)
                        .fillMaxWidth()
                        .clickable { onSelect(menuItem.route.first()) }
                        .then(if (isSelected == index) Modifier.background(MaterialTheme.colors.secondary) else Modifier)
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        tint = if (isSelected == index) MaterialTheme.colors.onSecondary else MaterialTheme.colors.secondary,
                        painter = painterResource(id = menuItem.icon),
                        contentDescription = menuItem.title,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.subtitle2
                    )

                    if (menuItem.route.contains(CartFeature.target) && cartCount.count > 0) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "+${cartCount.count}",
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected == index) MaterialTheme.colors.onSecondary else MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }

                    if (menuItem.route.contains("notifications") && notificationCount > 0) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "+$notificationCount",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .clickable { onSelect("about") }
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                tint = MaterialTheme.colors.secondary,
                painter = painterResource(id = R.drawable.ic_about),
                contentDescription = "О приложении",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = stringResource(id = R.string.title_about),
                style = MaterialTheme.typography.subtitle2
            )
        }

    }
}

@Preview
@Composable
public fun DrawerPreview() {
    val menuItems: List<UiDrawerMenuItem> = listOf(
        UiDrawerMenuItem(R.drawable.ic_home, "Главная", setOf("home")),
        UiDrawerMenuItem(R.drawable.ic_menu, "Меню", setOf("dishes_menu")),
        UiDrawerMenuItem(R.drawable.ic_favorite, "Избраное", setOf("favorite")),
        UiDrawerMenuItem(R.drawable.ic_baseline_shopping_cart_24, "Корзина", setOf("screen_cart")),
        UiDrawerMenuItem(R.drawable.ic_user, "Профиль", setOf("profile")),
        UiDrawerMenuItem(R.drawable.ic_orders, "Заказы", setOf("order")),
        UiDrawerMenuItem(R.drawable.ic_notification, "Уведомления", setOf("notifications")),
    )

    //val user = UiUser(fio = "Сидоров Иван", email = "sidorov.ivan@mail.ru")

    AppTheme {
        NavigationDrawer(
            currentRoute = "home",
            menuItems = menuItems,
            notificationCount = 7,
            cartCount = CartCount(8),
            onLogout = {}
        ) {
        }
    }
}
