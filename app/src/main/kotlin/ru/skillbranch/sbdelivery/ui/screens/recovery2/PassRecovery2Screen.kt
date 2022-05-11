package ru.skillbranch.sbdelivery.ui.screens.recovery2

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.ui.components.ProgressBar


@Composable
public fun PassRecovery2Screen(vm: PassRecovery2ViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    PassRecovery2Form(state, vm::mutate)
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun PassRecovery2Form(
    state: PassRecovery2Feature.State,
    mutate: (PassRecovery2Feature.Msg) -> Unit
) {
    val scrollState = rememberScrollState()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        val (pinCode) = createRefs()
        Box(modifier = Modifier.constrainAs(pinCode) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
        ) {
            PinCodeRow(mutate)
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun PinCodeRow(mutate: (PassRecovery2Feature.Msg) -> Unit) {
    val (editValue, setEditValue) = remember { mutableStateOf("") }
    val otpLength = 4
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    TextField(
        value = editValue,
        onValueChange = {
            if (it.length <= otpLength) setEditValue(it)
            if (it.length == otpLength) mutate(PassRecovery2Feature.Msg.Recovery2(recoveryCode = editValue))
        },
        modifier = Modifier
            .size(0.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        (0 until otpLength).map { index ->
            PinCodeCell(
                modifier = Modifier
                    .border(1.dp, Color.DarkGray)
                    .size(36.dp)
                    .clickable {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                value = editValue.getOrNull(index)?.toString() ?: "",
                isCursorVisible = editValue.length == index
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
    }

}

@Composable
public fun PinCodeCell(
    modifier: Modifier,
    value: String,
    isCursorVisible: Boolean = false
) {
    val cursorSymbol = remember { mutableStateOf("") }

    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            launch {
                delay(300)
                cursorSymbol.value = (if (cursorSymbol.value.isEmpty()) "|" else "")
            }
        }
    }

    Box(modifier = modifier) {
        Text(
            text = if (isCursorVisible) cursorSymbol.value else value,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
@Preview
public fun PreviewPassRecovery2() {
    AppTheme {
        PassRecovery2Form(PassRecovery2Feature.State()) {}
    }
}
