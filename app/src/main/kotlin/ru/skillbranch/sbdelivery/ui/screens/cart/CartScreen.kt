package ru.skillbranch.sbdelivery.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.COMPOSE_NOT_FOUND
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature.Msg.*


@Composable
public fun CartScreen(vm: CartViewModel) {
    val state = vm.state.collectAsState().value

    val promoCodeError = remember { mutableStateOf("") }

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())

    if (state.dishes.isNotEmpty()) {

        Column {
            Row(
                modifier = Modifier
                    //.background(color = Green)
                    .weight(1f)
            ) {
                LazyGridSwipe(
                    items = state.dishes,
                    cols = 1,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 0.dp
                    ),
                    itemSwipe = { dish -> vm.mutate(Delete(dish.id)) }
                ) { dish ->
                    CardCartItem(
                        dish = dish,
                        modifier = Modifier,
                        onIncrement = { vm.mutate(Increment(dish.id)) },
                        onDecrement = { vm.mutate(Decrement(dish.id)) },
                        onClick = {}
                    )
                }

            }
            Row(
                modifier = Modifier.wrapContentHeight()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(3f)
                                .padding(end = 8.dp)
                        ) {

                            Field(
                                value = state.promocode,
                                error = promoCodeError,
                                enabled = !state.promoInited,
                                readOnly = state.promoInited,
                                keyboardType = KeyboardType.Text,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colors.onPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colors.primary,
                                        shape = RoundedCornerShape(percent = 10)
                                    )
                            ) {
                                vm.mutate(SetPromoCode(it))
                            }
                        }

                        Column(modifier = Modifier.weight(2f)) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                onClick = {
                                    if (!state.promoInited) {
                                        vm.mutate(ApplyPromoCode)
                                    } else {
                                        vm.mutate(CancelPromoCode)
                                    }
                                }
                            ) {
                                Text(
                                    text = if (!state.promoInited)
                                        stringResource(R.string.labelApply)
                                    else {
                                        stringResource(R.string.labelCancel)
                                    }
                                )
                            }
                        }
                    }




                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = state.promotext)
                    }



                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Итого")
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "${state.total} р.")
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .defaultMinSize(32.dp)
                    ) {

                        ButtonV1(
                            text = stringResource(R.string.labelOrderProcessing),
                            enabled = true
                        ) {
                            if (vm.appSharedPreferences.userId.isNotBlank()) {
                                vm.mutate(OpenOrderProcessing)
                            } else {
                                vm.mutate(OpenLogin)
                            }

                        }
                    }
                }
            }
        }
    } else {
        COMPOSE_NOT_FOUND()
    }
}
