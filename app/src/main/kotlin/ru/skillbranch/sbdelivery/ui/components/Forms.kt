package ru.skillbranch.sbdelivery.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import ru.skillbranch.common.validation.StringValidators
import ru.skillbranch.common.validation.ValidatorAggregated
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.StringsRes

public object FormsValidators {

    public val fieldName: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.isRUorEN, StringsRes.get(R.string.E_ONLY_RU_EN)),
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val fieldSName: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.isRUorEN, StringsRes.get(R.string.E_ONLY_RU_EN)),
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val fieldEmail: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.isEmail, StringsRes.get(R.string.E_NOT_VALID_EMAIL)),
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val fieldPassword: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val fieldDigit: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.hasDigits, StringsRes.get(R.string.E_ONLY_DIGIT)),
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val fieldAddress: ValidatorAggregated<String> = ValidatorAggregated(
        Pair(StringValidators.isNotEmpty, StringsRes.get(R.string.E_EMPTY_FIELD)),
    )
    public val stub: ValidatorAggregated<String> = ValidatorAggregated(
    )
}

// region Поле ввода
@Composable
public fun Field(
    value: String,
    error: MutableState<String>,
    keyboardType: KeyboardType,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    validator: ValidatorAggregated<String> = FormsValidators.stub,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(
        color = if (enabled) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onBackground,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    ),
    length: Int? = null,
    visibility: (MutableState<Boolean>)? = null,
    onValueChange: ((String) -> Unit)? = null
) {

    OutlinedTextField(
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        value = value,
        onValueChange = {
            error.value = ""
            validator.validate(it) { errMsg -> error.value = errMsg }
            if (length != null && length > 0 && it.isNotEmpty()) {
                onValueChange?.invoke(it.substring(0, length))
            } else {
                onValueChange?.invoke(it)
            }
        },
        modifier = Modifier
            .background(
                color = MaterialTheme.colors.onPrimary,
                shape = RoundedCornerShape(percent = 10)
            )
            .onFocusEvent { focusEvent ->
                if (!focusEvent.isFocused && value.isEmpty()) error.value = ""
            }
            .then(modifier),
        isError = error.value.isNotEmpty(),

        trailingIcon = {
            if (visibility != null) {
                IconButton(onClick = { visibility.value = !visibility.value }) {
                    Icon(
                        imageVector = if (visibility.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "visibility",
                        tint = Color.Red
                    )
                }
            }
        },
        visualTransformation = if (visibility?.value == true) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.surface,
            unfocusedBorderColor = MaterialTheme.colors.primary
        )
    )
}
// endregion

// region Поле ввода
@Composable
public fun Field(
    value: MutableState<String>,
    error: MutableState<String>,
    keyboardType: KeyboardType,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    validator: ValidatorAggregated<String> = FormsValidators.stub,
    textStyle: TextStyle = TextStyle(
        color = if (enabled) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onBackground,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    ),
    length: Int? = null,
    visibility: (MutableState<Boolean>)? = null,
    onValueChange: ((String) -> Unit)? = null
) {

    OutlinedTextField(
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        value = value.value,
        onValueChange = {
            error.value = ""
            validator.validate(it) { errMsg -> error.value = errMsg }
            if (length != null && length > 0 && it.isNotEmpty()) {
                value.value = it.substring(0, length)
            } else {
                value.value = it
            }

            onValueChange?.invoke(it)
        },
        modifier = Modifier
            .background(
                color = MaterialTheme.colors.onPrimary,
                shape = RoundedCornerShape(percent = 10)
            )
            .onFocusEvent { focusEvent ->
                if (!focusEvent.isFocused && value.value.isEmpty()) error.value = ""
            }
            .then(modifier),
        isError = error.value.isNotEmpty(),

        trailingIcon = {
            if (visibility != null) {
                IconButton(onClick = { visibility.value = !visibility.value }) {
                    Icon(
                        imageVector = if (visibility.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "visibility",
                        tint = Color.Red
                    )
                }
            }
        },
        visualTransformation = if (visibility?.value == true) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.surface,
            unfocusedBorderColor = MaterialTheme.colors.primary
        )
    )
}
// endregion

// region Метка поля
@Composable
public fun FieldLabel(
    value: String,
    error: String,
    modifier: Modifier = Modifier,
    colorMain: Color = MaterialTheme.colors.onPrimary,
    colorError: Color = MaterialTheme.colors.error,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                color = if (error.isNotEmpty()) colorError else colorMain,
                fontSize = 14.sp,
                maxLines = 1,
                style = TextStyle(fontWeight = FontWeight.Bold),
                text = value,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(2f)) {
            if (error.isNotEmpty()) {
                Text(
                    color = colorError,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.End),
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    text = error,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
//endregion

@SuppressLint("UnrememberedMutableState")
@Composable
public fun FieldRow(
    prefix: String,
    fieldName: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val stateValue = mutableStateOf(sharedPref.getString(fieldName, "")!!)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = prefix,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = stateValue.value,
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colors.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = {
                sharedPref.edit {
                    putString(fieldName, it)
                    commit()
                }

                stateValue.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(percent = 10)
                ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.surface,
                unfocusedBorderColor = MaterialTheme.colors.primary
            )
        )

    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
public fun TextRow(
    prefix: String,
    fieldName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val stateValue = mutableStateOf(sharedPref.getString(fieldName, "")!!)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = prefix,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = stateValue.value,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(percent = 10)
                )
        )
    }
}

@Composable
public fun TextRow1(
    prefix: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = prefix,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = value,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(percent = 10)
                )
        )
    }
}
