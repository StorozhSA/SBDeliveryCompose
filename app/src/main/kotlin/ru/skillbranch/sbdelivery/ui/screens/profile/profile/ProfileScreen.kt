package ru.skillbranch.sbdelivery.ui.screens.profile.profile

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
import ru.skillbranch.sbdelivery.models.network.domains.ReqUserProfile
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.Msg.IsShowDialog


@Composable
public fun ProfileScreen(vm: ProfileViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())
    ProfileForm(state, vm::mutate)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun ProfileForm(
    state: ProfileFeature.State,
    mutate: (ProfileFeature.Msg) -> Unit
) {
    val name = rememberSaveable { mutableStateOf(state.name) }
    val lname = rememberSaveable { mutableStateOf(state.lname) }
    val email = rememberSaveable { mutableStateOf(state.email) }

    val nameError = rememberSaveable { mutableStateOf("") }
    val lnameError = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }
    val isShowDialog = state.isShowDialog
    val isEdit = state.isEdit

    val keyboardController = LocalSoftwareKeyboardController.current

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value =
            nameError.value.isEmpty() && lnameError.value.isEmpty() && emailError.value.isEmpty() && (name.value != state.name || lname.value != state.lname || email.value != state.email)
    }

    if (isShowDialog) ProfileDialogChPswd(
        state = state,
        keyboardController = keyboardController,
        mutate = mutate
    )

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
            enabled = isEdit && !state.isLoading(),
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
            enabled = isEdit && !state.isLoading(),
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
            enabled = isEdit && !state.isLoading(),
            validator = FormsValidators.fieldEmail,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        ) { buttonEnabledCheck() }
        Spacer(modifier = Modifier.size(16.dp))
        // endregion

        // region Buttons
        if (isEdit) {
            buttonEnabledCheck()
            ButtonV1(
                text = stringResource(R.string.labelSave),
                enabled = buttonSaveEnabled.value && !state.isLoading(),
            ) {
                keyboardController?.hide()
                mutate(
                    ProfileFeature.Msg.ChangeProfile(
                        ReqUserProfile(
                            email = email.value,
                            firstName = name.value,
                            lastName = lname.value
                        )
                    )
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            ButtonV2(
                text = stringResource(R.string.labelCancel),
                enabled = true,
            ) {
                mutate(ProfileFeature.Msg.IsEdit(false))
                name.value = state.name
                lname.value = state.lname
                email.value = state.email
                nameError.value = ""
                lnameError.value = ""
                emailError.value = ""
            }
        } else {
            ButtonV1(
                text = stringResource(R.string.labelChange),
                enabled = true,
            ) {
                mutate(ProfileFeature.Msg.IsEdit(true))
            }
            Spacer(modifier = Modifier.size(16.dp))
            ButtonV2(
                text = stringResource(R.string.labelChangePassword),
                enabled = true,
            ) {
                mutate(IsShowDialog(true))
            }
        }
        Spacer(Modifier.size(LocalConfiguration.current.screenHeightDp.dp / 3 + 30.dp))
        // endregion
    }
}


@Composable
@Preview
public fun PreviewProfile() {
    AppTheme {
        ProfileForm(ProfileFeature.State()) {}
    }
}
