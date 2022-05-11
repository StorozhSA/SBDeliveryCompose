package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature


@Composable
public fun DishPrice(
    price: Int,
    modifier: Modifier = Modifier,
    amount: Int = 1,
    oldPrice: Int? = null,
    fontSize: Int = 24,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (oldPrice != null) {
            Text(
                text = "${oldPrice * amount} Р",
                color = MaterialTheme.colors.onPrimary,
                textDecoration = TextDecoration.LineThrough,
                style = TextStyle(fontWeight = FontWeight.ExtraLight),
                fontSize = fontSize.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = "${price * amount} Р",
            color = MaterialTheme.colors.secondary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = fontSize.sp
        )
        Spacer(
            modifier = Modifier
                .defaultMinSize(minWidth = 16.dp)
                .weight(1f)
        )
        Stepper(
            amount = amount,
            state = DishFeature.State(),
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )
    }
}


@Preview
@Composable
public fun PricePreview() {
    AppTheme {
        DishPrice(60, oldPrice = 100, amount = 5, onDecrement = {}, onIncrement = {})
    }
}
