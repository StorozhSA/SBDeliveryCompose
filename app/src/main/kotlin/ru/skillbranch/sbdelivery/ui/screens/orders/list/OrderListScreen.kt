package ru.skillbranch.sbdelivery.ui.screens.orders.list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.COMPOSE_NOT_FOUND
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.convertLongToTime
import ru.skillbranch.sbdelivery.models.database.domains.*
import ru.skillbranch.sbdelivery.ui.components.LazyGrid
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Msg.GotoOrder


@Composable
public fun OrderListScreen(vm: OrderListViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    OrderListForm(state, vm::mutate)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun OrderListForm(
    state: OrderListFeature.State,
    mutate: (Msg) -> Unit
) {
    var completed by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {

        // region Buttons
        Row {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { completed = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (completed) MaterialTheme.colors.background else MaterialTheme.colors.secondary,
                        contentColor = Color.White
                    ),
                ) {
                    Text(stringResource(id = R.string.labelActual))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { completed = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (completed) MaterialTheme.colors.secondary else MaterialTheme.colors.background,
                        contentColor = Color.White
                    ),
                ) {
                    Text(stringResource(id = R.string.labelDelivered))
                }
            }
        }
        // endregion

        // region List
        Row {
            val items = state.orders.filter { order -> order.order.completed == completed }
            if (items.isNotEmpty()) {
                LazyGrid(
                    items = items,
                    cols = 1,
                    cellsPadding = 0.dp,
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        end = 0.dp,
                        top = 8.dp,
                        bottom = 0.dp
                    )
                ) { CardOrder(order = it) { mutate(GotoOrder(it)) } }
            } else {
                COMPOSE_NOT_FOUND()
            }
        }
        // endregion
    }
}


@Composable
public fun CardOrder(
    order: Order,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minWidth = 160.dp)
        .padding(bottom = 2.dp)
        .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 16.dp)) {

            Row {
                Text(
                    text = convertLongToTime(order.order.createdAt),
                    fontSize = 12.sp,
                    style = TextStyle(fontWeight = FontWeight.Light),
                    maxLines = 1
                )
            }

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.order.id,
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.secondary,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                Column(
                    horizontalAlignment = End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${order.order.total} Р",
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.secondary,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
            }

            Row {

                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        text = order.order.address,
                        fontSize = 12.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        maxLines = 3
                    )
                }

                Column(
                    horizontalAlignment = End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = order.status.name,
                        fontSize = 12.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Suppress("SpellCheckingInspection")
@Preview
@Composable
public fun CardOrderPreview() {
    AppTheme {
        CardOrder(
            order = Order(
                order = EOrder(
                    id = "111111",
                    total = 23,
                    active = true,
                    address = "ул. Стара Загора, 82",
                    completed = true,
                    statusId = "Delivery",
                    createdAt = 0,
                    updatedAt = 0
                ),
                status = EOrderStatus(
                    id = "ttttt",
                    active = true,
                    cancelable = true,
                    createdAt = 0,
                    name = "Доставляется",
                    updatedAt = 0
                ),
                dishes = listOf(
                    Dish(
                        dishId = "4455",
                        orderId = "111111",
                        price = 34,
                        amount = 3,
                        additionalInfo = AdditionalInfo(
                            name = "Колбаса",
                            image = ""
                        )
                    )
                )
            )
        ) {

        }
    }
}


