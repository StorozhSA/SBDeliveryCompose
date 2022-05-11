package ru.skillbranch.sbdelivery.ui.screens.profile.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.StringsRes
import ru.skillbranch.sbdelivery.models.network.domains.ReqNewPassword
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.Msg.IsShowDialog


@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun ProfileDialogChPswd(
    state: ProfileFeature.State,
    keyboardController: SoftwareKeyboardController? = null,
    mutate: (ProfileFeature.Msg) -> Unit
) {
    val oldPassword = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(true) }
    val confirmPassword = rememberSaveable { mutableStateOf("") }

    val oldPasswordError = rememberSaveable { mutableStateOf("") }
    val passwordError = rememberSaveable { mutableStateOf("") }
    val confirmPasswordError = rememberSaveable { mutableStateOf("") }
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }


    fun buttonEnabledCheck() {
        buttonSaveEnabled.value =
            passwordError.value.isEmpty() &&
                confirmPasswordError.value.isEmpty() &&
                oldPasswordError.value.isEmpty() &&
                password.value.isNotBlank() &&
                confirmPassword.value.isNotBlank() &&
                oldPassword.value.isNotBlank()
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

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        ProgressBar(state.isLoading())

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                // region Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.labelChangePassword),
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { mutate(IsShowDialog(false)) },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            tint = MaterialTheme.colors.onBackground,
                            painter = painterResource(R.drawable.ic_baseline_close_24),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                // endregion

                // region Старый Пароль
                FieldLabel(
                    value = stringResource(R.string.label_password_old),
                    error = oldPasswordError.value,
                    colorMain = MaterialTheme.colors.primary,
                )
                Field(
                    value = oldPassword,
                    error = oldPasswordError,
                    enabled = !state.isLoading(),
                    visibility = passwordVisibility,
                    validator = FormsValidators.fieldPassword,
                    keyboardType = KeyboardType.Password
                ) {
                    passwordsEqualsCheck()
                    buttonEnabledCheck()
                }
                Spacer(modifier = Modifier.size(16.dp))
                // endregion

                // region Пароль
                FieldLabel(
                    value = stringResource(R.string.label_password_new),
                    error = passwordError.value,
                    colorMain = MaterialTheme.colors.primary,
                )
                Field(
                    value = password,
                    error = passwordError,
                    enabled = !state.isLoading(),
                    visibility = passwordVisibility,
                    validator = FormsValidators.fieldPassword,
                    keyboardType = KeyboardType.Password
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
                    colorMain = MaterialTheme.colors.primary,
                )
                Field(
                    value = confirmPassword,
                    error = confirmPasswordError,
                    enabled = !state.isLoading(),
                    visibility = passwordVisibility,
                    validator = FormsValidators.fieldPassword,
                    keyboardType = KeyboardType.Password
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
                    mutate(
                        ProfileFeature.Msg.ChangePassword(
                            ReqNewPassword(
                                newPassword = password.value,
                                oldPassword = oldPassword.value
                            )
                        )
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
public fun ProfileDialogChPswdPreview() {
    AppTheme {
        ProfileDialogChPswd(ProfileFeature.State()) {}
    }
}
