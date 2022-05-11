package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R

@Composable
public fun CartButton(
    cartCount: Int,
    onClickCart: () -> Unit
) {
    IconButton(
        onClick = onClickCart,
        content = {
            Icon(
                tint = MaterialTheme.colors.secondary,
                painter = painterResource(R.drawable.ic_baseline_shopping_cart_24),
                contentDescription = "Cart"
            )
            if (cartCount > 0) {
                Text(
                    text = if (cartCount < 99) "$cartCount" else "∞",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier
                        .offset(8.dp, (-8).dp)
                        .size(16.dp)
                        .background(
                            alpha = 0.8f,
                            brush = SolidColor(MaterialTheme.colors.secondaryVariant),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    )
}

@Preview
@Composable
public fun CartButtonInfinityPreview() {
    AppTheme {
        CartButton(100) {}
    }
}

@Preview
@Composable
public fun CartButtonTenPreview() {
    AppTheme {
        CartButton(10) {}
    }
}

@Preview
@Composable
public fun CartButtonEmptyPreview() {
    AppTheme {
        CartButton(0) {}
    }
}


