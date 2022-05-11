package ru.skillbranch.sbdelivery.ui.screens.orders.processing

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.components.ButtonV1
import ru.skillbranch.sbdelivery.ui.components.FieldRow
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.components.TextRow
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Msg.*


@Composable
public fun OrderProcessingScreen(vm: OrderProcessingViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    OrderProcessingForm(state, vm::mutate)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun OrderProcessingForm(
    state: OrderProcessingFeature.State,
    mutate: (Msg) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Address
        TextRow(
            prefix = stringResource(R.string.labelAddress) + ":",
            fieldName = "address",
            modifier = Modifier.padding(end = 8.dp)
        )

        // Buttons
        Row(modifier = Modifier.fillMaxWidth()) {

            ButtonV1(
                text = stringResource(R.string.labelWrite),
                enabled = !state.isLoading(),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)

            ) { mutate(OpenAddressText) }

            ButtonV1(
                text = stringResource(R.string.labelSelectFromMap),
                enabled = !state.isLoading(),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)

            ) { mutate(OpenAddressMap) }
        }

        // Entrance
        FieldRow(
            prefix = stringResource(R.string.labelEntrance) + ":",
            fieldName = "entrance",
            enabled = !state.isLoading(),
            modifier = Modifier.padding(end = 8.dp)
        )

        // Floor
        FieldRow(
            prefix = stringResource(R.string.labelFloor) + ":",
            fieldName = "floor",
            enabled = !state.isLoading(),
            modifier = Modifier.padding(end = 8.dp)
        )

        // Apartment
        FieldRow(
            prefix = stringResource(R.string.labelApartment) + ":",
            fieldName = "apartment",
            enabled = !state.isLoading(),
            modifier = Modifier.padding(end = 8.dp)
        )

        // Intercom
        FieldRow(
            prefix = stringResource(R.string.labelIntercom) + ":",
            fieldName = "intercom",
            enabled = !state.isLoading(),
            modifier = Modifier.padding(end = 8.dp)
        )

        // Comment
        FieldRow(
            prefix = stringResource(R.string.labelComment) + ":",
            fieldName = "comment",
            enabled = !state.isLoading(),
            modifier = Modifier.padding(end = 8.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )

        // Order processing
        ButtonV1(
            text = stringResource(R.string.labelOrderProcessing),
            enabled = !state.isLoading(),
            modifier = Modifier.padding(8.dp)

        ) { mutate(CreateNewOrder) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
public fun PreviewOrderProcessing() {
    AppTheme {
        OrderProcessingForm(OrderProcessingFeature.State()) {}
    }
}



