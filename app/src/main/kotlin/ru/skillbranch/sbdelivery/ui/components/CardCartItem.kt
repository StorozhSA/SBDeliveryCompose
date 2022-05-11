package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined


@OptIn(ExperimentalCoilApi::class)
@Composable
public fun CardCartItem(
    dish: CartItemJoined,
    modifier: Modifier = Modifier,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minWidth = 160.dp)
        .clickable { onClick() }
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(0.45f)
                //.padding(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = dish.image,
                        builder = {
                            error(R.drawable.img_empty_place_holder)
                            placeholder(R.drawable.img_empty_place_holder)
                        }
                    ),
                    contentDescription = dish.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.44f)
                        .fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(8.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onPrimary,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        text = dish.name,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.defaultMinSize(minHeight = 20.dp)
                ) {

                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        CardCartItemStepper(
                            amount = dish.amount,
                            modifier = Modifier,
                            onDecrement = onDecrement,
                            onIncrement = onIncrement
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${dish.price * dish.amount} Р",
                            color = MaterialTheme.colors.secondary,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
public fun CardCartItemPreview() {

    val dish = CartItemJoined(
        id = "0",
        image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
        name = "Бургер \"Америка\"",
        price = 200,
        amount = 1
    )

    AppTheme {
        CardCartItem(
            dish = dish,
            onIncrement = {},
            onDecrement = {}
        ) {}
    }
}
