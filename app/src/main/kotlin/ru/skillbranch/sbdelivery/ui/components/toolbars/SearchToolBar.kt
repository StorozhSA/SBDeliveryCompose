package ru.skillbranch.sbdelivery.ui.components.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.components.CartButton


@Composable
public fun SearchToolbar(
    title: String,
    input: String,
    cartCount: Int = 0,
    canCart: Boolean = false,
    isSearch: Boolean = false,
    canBack: Boolean = false,
    suggestions: Map<String, Int> = emptyMap(),
    onInput: ((query: String) -> Unit)? = null,
    onSubmit: ((query: String) -> Unit)? = null,
    onSuggestionClick: ((query: String) -> Unit)? = null,
    onSearchToggle: (() -> Unit)? = null,
    onBackClick: () -> Unit,
    onClickCart: () -> Unit,
    onDrawer: () -> Unit
) {
    //val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    Column {
        TopAppBar(
            navigationIcon = {
                if (!canBack) {
                    IconButton(
                        onClick = onDrawer,
                        content = {
                            Icon(
                                tint = MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_baseline_menu_24),
                                contentDescription = "home"
                            )
                        }
                    )
                } else {
                    IconButton(
                        onClick = {
                            onBackClick()
                            //dispatcher.onBackPressed()
                        },
                        content = {
                            Icon(
                                tint = MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                                contentDescription = "back"
                            )
                        }
                    )
                }

            },
            title = {
                if (!isSearch) Text(
                    text = title,
                    color = MaterialTheme.colors.onPrimary
                ) else SearchField(
                    input = input,
                    placeholder = "Поиск",
                    onInput = onInput,
                    onSubmit = onSubmit
                )
            },
            actions = {
                IconButton(
                    onClick = { onSearchToggle?.invoke() },
                    content = {
                        Icon(
                            tint = if (!isSearch) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary,
                            painter = painterResource(if (!isSearch) R.drawable.ic_search_dishes else R.drawable.ic_baseline_close_24),
                            contentDescription = null
                        )
                    }
                )
                if (canCart) {
                    CartButton(cartCount = cartCount, onClickCart = onClickCart)
                }
            }
        )
        if (suggestions.isNotEmpty()) {
            BoxWithConstraints(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                        .width(maxWidth)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        suggestions.forEach {
                            SuggestionItem(it.key, it.value) { title ->
                                onSuggestionClick?.invoke(title)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
public fun SuggestionItem(
    title: String,
    amount: Int,
    onSuggestionClick: ((query: String) -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSuggestionClick?.invoke(title) }
            .padding(16.dp, vertical = 24.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$amount",
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier
                .defaultMinSize(minWidth = 16.dp)
                .background(
                    MaterialTheme.colors.secondary,
                    RoundedCornerShape(50),
                )
                .padding(horizontal = 8.dp)
        )
    }
}

@Preview
@Composable
public fun DefaultSearchPreviewSuggestionItem() {
    AppTheme {
        SuggestionItem(title = "Пицца", amount = 3, onSuggestionClick = {})
    }
}


@Preview
@Composable
public fun DefaultSearchPreviewFalse() {
    AppTheme {
        SearchToolbar(
            title = "Not can back",
            input = "Привет",
            cartCount = 10,
            isSearch = false,
            canBack = false,
            onBackClick = {},
            onClickCart = {}
        ) {}
    }
}


@Preview
@Composable
public fun DefaultSearchPreviewTrue() {
    AppTheme {
        SearchToolbar(
            title = "Not can back",
            input = "Привет",
            cartCount = 10,
            isSearch = true,
            canBack = false,
            onBackClick = {},
            onClickCart = {}
        ) {}
    }
}


@Preview
@Composable
public fun DefaultSearchPreviewSuggestion() {
    AppTheme {
        SearchToolbar(
            title = "Not can back",
            input = "Привет",
            cartCount = 10,
            isSearch = true,
            suggestions = mapOf("Пицца" to 4, "Суп" to 2),
            canBack = false,
            onBackClick = {},
            onClickCart = {}
        ) {}
    }
}
