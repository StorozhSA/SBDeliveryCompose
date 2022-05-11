package ru.skillbranch.sbdelivery.ui.screens.profile.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.network.domains.ReqLogin
import ru.skillbranch.sbdelivery.ui.components.*


@Composable
public fun LoginScreen(vm: LoginViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    LoginForm(state, vm::mutate)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun LoginForm(
    state: LoginFeature.State,
    mutate: (LoginFeature.Msg) -> Unit
) {

    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }
    val email = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val password = rememberSaveable { mutableStateOf("") }
    val passwordError = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(true) }

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value = emailError.value.isEmpty() && passwordError.value.isEmpty()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        val (flemail, femail, flpassword, fpassword, blogin, breg, spacer, forgot) = createRefs()

        createVerticalChain(
            flemail,
            femail,
            flpassword,
            fpassword,
            blogin,
            breg,
            spacer,
            chainStyle = ChainStyle.Packed
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


        // region Пароль
        FieldLabel(
            value = stringResource(R.string.labelPassword),
            error = passwordError.value,
            modifier = Modifier
                .constrainAs(flpassword) { centerVerticallyTo(parent) }
                .padding(top = 8.dp)
        )
        Field(
            value = password,
            error = passwordError,
            enabled = !state.isLoading(),
            visibility = passwordVisibility,
            validator = FormsValidators.fieldPassword,
            keyboardType = KeyboardType.Password,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(fpassword) { centerVerticallyTo(parent) }
            //.padding(bottom = 24.dp)
        ) {
            buttonEnabledCheck()
        }
        // endregion


        // region Button Login
        ButtonV1(
            text = stringResource(R.string.labelLogin),
            enabled = buttonSaveEnabled.value && !state.isLoading(),
            modifier = Modifier
                .constrainAs(blogin) { centerVerticallyTo(parent) }
                .padding(bottom = 8.dp, top = 16.dp)
        ) {
            keyboardController?.hide()
            mutate(
                LoginFeature.Msg.Login(
                    ReqLogin(
                        email = email.value,
                        password = password.value
                    )
                )
            )
        }
        // endregion

        // region Button Registration
        ButtonV1(
            text = stringResource(R.string.labelReg),
            enabled = !state.isLoading(),
            modifier = Modifier
                .constrainAs(breg) { centerVerticallyTo(parent) }
                .padding(bottom = 8.dp)
        ) {
            keyboardController?.hide()
            mutate(LoginFeature.Msg.GoToRegistration)
        }
        // endregion

        Spacer(
            modifier = Modifier
                .size(48.dp)
                .constrainAs(spacer) { centerVerticallyTo(parent) }
        )

        TextButton(
            onClick = { mutate(LoginFeature.Msg.GoToPassRecovery) },
            modifier = Modifier
                .constrainAs(forgot) {
                    //top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(
                color = MaterialTheme.colors.onPrimary,
                fontSize = 18.sp,
                maxLines = 1,
                style = TextStyle(fontWeight = FontWeight.Bold),
                text = stringResource(id = R.string.label_forgot_password),
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
@Preview
public fun PreviewLogin() {
    AppTheme {
        LoginForm(LoginFeature.State()) {}
    }
}
