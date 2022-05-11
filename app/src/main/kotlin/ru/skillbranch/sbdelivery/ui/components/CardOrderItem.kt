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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.database.domains.Dish


@OptIn(ExperimentalCoilApi::class)
@Composable
public fun CardOrderItem(
    dish: Dish,
    modifier: Modifier = Modifier,
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
                        data = dish.additionalInfo.image,
                        builder = {
                            error(R.drawable.img_empty_place_holder)
                            placeholder(R.drawable.img_empty_place_holder)
                        }
                    ),
                    contentDescription = dish.additionalInfo.name,
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
                        text = dish.additionalInfo.name,
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
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.onPrimary,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            text = "${dish.amount} шт.",
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${dish.price} Р",
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
