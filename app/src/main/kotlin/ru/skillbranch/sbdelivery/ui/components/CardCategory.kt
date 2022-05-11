package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem


@OptIn(ExperimentalCoilApi::class)
@Composable
public fun CardCategory(
    item: UiCategoryItem,
    modifier: Modifier = Modifier,
    onClick: (UiCategoryItem) -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minWidth = 160.dp)
        .aspectRatio(1f)
        .clickable { onClick(item) }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val painter = rememberImagePainter(
                data = item.icon,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.img_empty_place_holder)
                    error(R.drawable.img_empty_place_holder)
                }
            )

            Icon(
                painter = painter,
                contentDescription = item.name,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = item.name,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = W700,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
public fun MenuPreview() {
    AppTheme {
        Row {
            CardCategory(
                item = UiCategoryItem(
                    id = "0",
                    name = "test test",
                    icon = "null",
                    order = 0,
                    parent = "null",
                    isDishRoot = false
                ),
                modifier = Modifier.requiredWidth(140.dp),
                onClick = {}
            )
            CardCategory(
                item = UiCategoryItem(
                    id = "1",
                    name = "test test test test test test",
                    icon = "null",
                    order = 0,
                    parent = "null",
                    isDishRoot = false
                ),
                modifier = Modifier.requiredWidth(140.dp),
                onClick = {}
            )
        }
    }
}
