package ru.skillbranch.sbdelivery.ui.components.toolbars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R


@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun SearchField(
    input: String,
    placeholder: String = "Поиск",
    onInput: ((query: String) -> Unit)? = null,
    onSubmit: ((query: String) -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val decoratedPlaceholder: @Composable ((Modifier) -> Unit)? =
        if (input.isEmpty()) {
            @Composable {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(0.75f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_dishes),
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colors.onPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        } else null

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.weight(1f),
            content = {
                decoratedPlaceholder?.invoke(Modifier.fillMaxWidth())

                BasicTextField(
                    value = input,
                    onValueChange = { onInput?.invoke(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(start = 2.dp, bottom = 2.dp),
                    singleLine = true,
                    textStyle = TextStyle.Default.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onPrimary
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSubmit?.invoke(input)
                            keyboardController?.hide()
                        },
                    ),
                )
            }
        )
    }
}


@Preview
@Composable
public fun SearchFieldEmptyPreview() {
    AppTheme {
        SearchField(input = "", onInput = {}, onSubmit = {})
    }
}


@Preview
@Composable
public fun SearchFieldWithTextPreview() {
    AppTheme {
        SearchField(input = "Пицца", onInput = {}, onSubmit = {})
    }
}
