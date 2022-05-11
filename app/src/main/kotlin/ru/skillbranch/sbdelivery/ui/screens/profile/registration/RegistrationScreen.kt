package ru.skillbranch.sbdelivery.ui.screens.profile.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.StringsRes
import ru.skillbranch.sbdelivery.models.network.domains.ReqRegister
import ru.skillbranch.sbdelivery.ui.components.*


@Composable
public fun RegistrationScreen(vm: RegistrationViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    RegistrationForm(state, vm::mutate)
}

@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun RegistrationForm(
    state: RegistrationFeature.State,
    mutate: (RegistrationFeature.Msg) -> Unit
) {
    val name = rememberSaveable { mutableStateOf("") }
    val lname = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(true) }
    val confirmPassword = rememberSaveable { mutableStateOf("") }

    val nameError = rememberSaveable { mutableStateOf("") }
    val lnameError = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf("") }
    val passwordError = rememberSaveable { mutableStateOf("") }
    val confirmPasswordError = rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value =
            nameError.value.isEmpty() &&
                lnameError.value.isEmpty() &&
                emailError.value.isEmpty() &&
                passwordError.value.isEmpty() &&
                confirmPasswordError.value.isEmpty()
    }


    fun passwordsEqualsCheck() {
        if (password.value != confirmPassword.value) {
            passwordError.value = StringsRes.get(R.string.E_NOT_EQ_PASS)
            confirmPasswordError.value = StringsRes.get(R.string.E_NOT_EQ_PASS)
        } else {
            passwordError.value = ""
            confirmPasswordError.value = ""
        }
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
    ) {

        // region Имя
        FieldLabel(
            value = stringResource(R.string.labelFirstName),
            error = nameError.value
        )
        Field(
            value = name,
            error = nameError,
            enabled = !state.isLoading(),
            validator = FormsValidators.fieldName,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        ) { buttonEnabledCheck() }
        Spacer(modifier = Modifier.size(16.dp))
        // endregion

        // region Фамилия
        FieldLabel(
            value = stringResource(R.string.labelLastName),
            error = lnameError.value
        )
        Field(
            value = lname,
            error = lnameError,
            enabled = !state.isLoading(),
            validator = FormsValidators.fieldSName,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        ) { buttonEnabledCheck() }
        Spacer(modifier = Modifier.size(16.dp))
        // endregion

        // region E-mail
        FieldLabel(
            value = stringResource(R.string.labelEmail),
            error = emailError.value
        )
        Field(
            value = email,
            error = emailError,
            enabled = !state.isLoading(),
            validator = FormsValidators.fieldEmail,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        ) { buttonEnabledCheck() }
        Spacer(modifier = Modifier.size(16.dp))
        // endregion

        // region Пароль
        FieldLabel(
            value = stringResource(R.string.labelPassword),
            error = passwordError.value
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
            error = confirmPasswordError.value
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

        // region Button Registration

        // region Button Login
        ButtonV1(
            text = stringResource(R.string.labelReg),
            enabled = buttonSaveEnabled.value && !state.isLoading(),
        ) {
            keyboardController?.hide()
            mutate(
                RegistrationFeature.Msg.Registration(
                    ReqRegister(
                        firstName = name.value,
                        lastName = lname.value,
                        email = email.value,
                        password = password.value
                    )
                )
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Spacer(Modifier.size(LocalConfiguration.current.screenHeightDp.dp / 3 + 30.dp))
        // endregion
    }
}


@Composable
@Preview
public fun PreviewRegistration() {
    AppTheme {
        RegistrationForm(RegistrationFeature.State()) {}
    }
}
