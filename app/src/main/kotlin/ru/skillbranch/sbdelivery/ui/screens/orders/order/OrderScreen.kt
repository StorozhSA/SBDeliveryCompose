package ru.skillbranch.sbdelivery.ui.screens.orders.order

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.convertLongToTime
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Msg

@Composable
public fun OrderScreen(vm: OrderViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    OrderForm(state, vm::mutate)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun OrderForm(
    state: OrderFeature.State,
    mutate: (Msg) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TextRow1(
            prefix = stringResource(R.string.labelStatus) + ":",
            value = state.order.status.name,
            modifier = Modifier.padding(end = 8.dp)
        )

        TextRow1(
            prefix = stringResource(R.string.labelAmount) + ":",
            value = state.order.order.total.toString(),
            modifier = Modifier.padding(end = 8.dp)
        )

        TextRow1(
            prefix = stringResource(R.string.labelAddress) + ":",
            value = state.order.order.address,
            modifier = Modifier.padding(end = 8.dp)
        )

        TextRow1(
            prefix = stringResource(R.string.labelDate) + ":",
            value = convertLongToTime(state.order.order.createdAt),
            modifier = Modifier.padding(end = 8.dp)
        )

        if (state.order.order.completed) {
            ButtonV1(
                text = stringResource(R.string.labelRepeatOrder),
                enabled = !state.isLoading(),
                modifier = Modifier.padding(8.dp)
            ) { /*mutate(OrderProcessingFeature.Msg.OpenAddressText)*/ }
        } else {
            ButtonV1(
                text = stringResource(R.string.labelCancelOrder),
                enabled = !state.isLoading() && state.order.status.cancelable && state.order.status.name.uppercase() != "ОТМЕНЕН",
                modifier = Modifier.padding(8.dp)
            ) { mutate(Msg.CancelOrder) }
        }

        TextRow1(
            prefix = stringResource(R.string.labelOrderContent) + ":",
            value = "",
            modifier = Modifier.padding(end = 8.dp)
        )

        Row(
            modifier = Modifier
                //.background(color = Green)
                .weight(1f)
        ) {
            LazyGrid(
                items = state.order.dishes,
                cols = 1,
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 0.dp
                )
            ) { dish ->
                CardOrderItem(
                    dish = dish,
                    modifier = Modifier,
                    onClick = {}
                )
            }

        }


    }


}




