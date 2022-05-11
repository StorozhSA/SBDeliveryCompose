package ru.skillbranch.sbdelivery.ui.screens.recovery1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.Msg.Recovery1


@Composable
public fun PassRecovery1Screen(vm: PassRecovery1ViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    PassRecovery1Form(state, vm::mutate)
}

@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun PassRecovery1Form(
    state: PassRecovery1Feature.State,
    mutate: (PassRecovery1Feature.Msg) -> Unit
) {

    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }
    val email = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value = emailError.value.isEmpty()
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {

        val (desc, flemail, femail, blogin) = createRefs()

        createVerticalChain(
            desc,
            flemail,
            femail,
            blogin,
            chainStyle = ChainStyle.Packed
        )

        Text(
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            style = TextStyle(fontWeight = FontWeight.Bold),
            text = stringResource(id = R.string.textRecovPassword),
            modifier = Modifier
                .constrainAs(desc) { centerVerticallyTo(parent) }
                .padding(bottom = 8.dp)
        )

        // region E-mail
        FieldLabel(
            value = stringResource(R.string.labelEmail),
            error = emailError.value,
            modifier = Modifier.constrainAs(flemail) { centerVerticallyTo(parent) }
        )

        Field(
            value = email,
            error = emailError,
            enabled = !state.isLoading(),
            validator = FormsValidators.fieldEmail,
            keyboardType = KeyboardType.Email,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(femail) { centerVerticallyTo(parent) }
            //.padding(bottom = 8.dp)
        ) { buttonEnabledCheck() }
        // endregion

        // region Button send email
        ButtonV1(
            text = stringResource(R.string.labelSend),
            enabled = buttonSaveEnabled.value && !state.isLoading(),
            modifier = Modifier
                .constrainAs(blogin) { centerVerticallyTo(parent) }
                .padding(bottom = 8.dp, top = 16.dp)
        ) {
            keyboardController?.hide()
            mutate(Recovery1(recoveryEmail = email.value))
        }
        // endregion
    }
}


@Composable
@Preview
public fun PreviewPassRecovery1() {
    AppTheme {
        PassRecovery1Form(PassRecovery1Feature.State()) {}
    }
}
