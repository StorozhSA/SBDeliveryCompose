package ru.skillbranch.sbdelivery.ui.screens.recovery3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.StringsRes
import ru.skillbranch.sbdelivery.ui.components.*


@Composable
public fun PassRecovery3Screen(vm: PassRecovery3ViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    PassRecovery3Form(state, vm::mutate)
}

@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun PassRecovery3Form(
    state: PassRecovery3Feature.State,
    mutate: (PassRecovery3Feature.Msg) -> Unit
) {
    val password = rememberSaveable { mutableStateOf("") }
    val confirmPassword = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }

    val passwordError = rememberSaveable { mutableStateOf("") }
    val confirmPasswordError = rememberSaveable { mutableStateOf("") }
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current


    fun buttonEnabledCheck() {
        buttonSaveEnabled.value =
            passwordError.value.isEmpty() &&
                confirmPasswordError.value.isEmpty() &&
                password.value.isNotBlank() &&
                confirmPassword.value.isNotBlank()
    }

    fun passwordsEqualsCheck() {
        if (password.value != confirmPassword.value) {
            passwordError.value = StringsRes.get(R.string.E_NOT_EQ_PASS)
            confirmPasswordError.value = StringsRes.get(R.string.E_NOT_EQ_PASS)
        } else {
            passwordError.value = ""
            confirmPasswordError.value = ""

            FormsValidators.fieldPassword.validate(password.value) {
                passwordError.value = it
            }

            FormsValidators.fieldPassword.validate(confirmPassword.value) {
                confirmPasswordError.value = it
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        val (pinCode) = createRefs()
        Column(modifier = Modifier.constrainAs(pinCode) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
        ) {
            // region Пароль
            FieldLabel(
                value = stringResource(R.string.label_password_new),
                error = passwordError.value,
                colorMain = MaterialTheme.colors.onPrimary,
            )
            Field(
                value = password,
                error = passwordError,
                enabled = !state.isLoading(),
                visibility = passwordVisibility,
                validator = FormsValidators.fieldPassword,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            ) {
                passwordsEqualsCheck()
                buttonEnabledCheck()
            }
            Spacer(modifier = Modifier.size(16.dp))
            // endregion

            // region Пароль подтверждение
            FieldLabel(
                value = stringResource(R.string.labelPasswordReplace),
                error = confirmPasswordError.value,
                colorMain = MaterialTheme.colors.onPrimary,
            )
            Field(
                value = confirmPassword,
                error = confirmPasswordError,
                enabled = !state.isLoading(),
                visibility = passwordVisibility,
                validator = FormsValidators.fieldPassword,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            ) {
                passwordsEqualsCheck()
                buttonEnabledCheck()
            }
            Spacer(modifier = Modifier.size(16.dp))
            // endregion

            ButtonV1(
                text = stringResource(R.string.labelSave),
                enabled = buttonSaveEnabled.value && !state.isLoading(),
            ) {
                keyboardController?.hide()
                mutate(PassRecovery3Feature.Msg.Recovery3(recoveryPass = password.value))
            }
        }
    }
}


@Composable
@Preview
public fun PreviewPassRecovery3() {
    AppTheme {
        PassRecovery3Form(PassRecovery3Feature.State()) {}
    }
}
