package ru.skillbranch.sbdelivery.ui.screens.address.text

import android.annotation.SuppressLint
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.common.extension.isNotNull
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.COMPOSE_NOT_FOUND
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.network.domains.ReqAddress
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature.Msg.SaveAddress
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature.Msg.SelectAddress


@Composable
public fun AddressTextScreen(vm: AddressTextViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    AddressTextForm(state, vm::mutate)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun AddressTextForm(
    state: AddressTextFeature.State,
    mutate: (AddressTextFeature.Msg) -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val rawAddress = state.reqAddress.address
    val rawAddressError = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value = state.resAddress.find {
            it.value == state.reqAddress.address
        }.isNotNull()
    }

    buttonEnabledCheck()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row {
            // region raw Address
            Field(
                value = rawAddress,
                error = rawAddressError,
                enabled = !state.isLoading(),
                validator = FormsValidators.fieldAddress,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth()
            ) {
                mutate(AddressTextFeature.Msg.SetReqAddress(ReqAddress(it)))
            }
            // endregion
        }

        Row(
            modifier = Modifier.weight(1f)
        ) {
            if (state.resAddress.isEmpty()) {
                COMPOSE_NOT_FOUND()
            } else {
                LazyGrid(
                    items = state.resAddress,
                    cols = 1,
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                    cellsPadding = 2.dp,
                    modifier = Modifier.fillMaxHeight(1f)
                ) { address ->
                    CardAddressItem(
                        address = address.value
                    ) { mutate(SelectAddress(address)) }
                }
            }
        }
        Row {
            ButtonV1(
                text = stringResource(R.string.labelSave),
                enabled = buttonSaveEnabled.value && !state.isLoading(),
            ) {
                keyboardController?.hide()
                mutate(SaveAddress)
                dispatcher.onBackPressed()
            }
        }
    }
}

@Composable
public fun CardAddressItem(
    address: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 64.dp)
            .clickable { onClick.invoke() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                fontSize = 18.sp,
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(fontWeight = FontWeight.Bold),
                text = address,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
@Preview
public fun PreviewAddressText() {
    AppTheme {
        AddressTextForm(AddressTextFeature.State()) {}
    }
}
