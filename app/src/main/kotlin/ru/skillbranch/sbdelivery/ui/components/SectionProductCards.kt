package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.data.domain.UiDishItem

@Composable
public fun SectionProductCards(
    dishes: List<UiDishItem>,
    title: String,
    modifier: Modifier = Modifier,
    limit: Int = 6,
    onClick: (UiDishItem) -> Unit,
    onAddToCart: (UiDishItem) -> Unit,
    onToggleLike: (UiDishItem) -> Unit
) {
    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (!expanded) "См. все" else "Свернуть",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(all = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!expanded) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Spacer(modifier = Modifier.width(8.dp)) }
                items(dishes.take(limit), { it.id }) { item ->
                    CardProduct(
                        dish = item,
                        onToggleLike = onToggleLike,
                        onAddToCart = onAddToCart,
                        onClick = onClick
                    )

                }
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        } else {
            Grid(
                items = dishes,
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp)
            ) {
                CardProduct(
                    dish = it,
                    onToggleLike = onToggleLike,
                    onAddToCart = onAddToCart,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
public fun ShimmerProductItem(
    colors: List<Color>,
    xShimmer: Float,
    yShimmer: Float,
    cardWidth: Dp,
    gradientWidth: Float,
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(xShimmer - gradientWidth, yShimmer - gradientWidth),
        end = Offset(xShimmer, yShimmer)
    )

    Card(modifier = modifier.width(cardWidth)) {
        Column(
            modifier = Modifier
                .size(100.dp)
                .background(brush)
        ) {
            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(brush = brush)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Spacer(
                        modifier = Modifier
                            .height(14.dp)
                            .width(cardWidth * 0.85f)
                            .background(brush = brush, shape = MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    //
                    Spacer(
                        modifier = Modifier
                            .height(14.dp)
                            .width(cardWidth * 0.85f)
                            .background(brush = brush, shape = MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    //
                    Spacer(
                        modifier = Modifier
                            .height(14.dp)
                            .width(cardWidth * 0.55f)
                            .background(brush = brush, shape = MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
public fun ShimmerSection(
    itemWidth: Dp,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "См. все",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .padding(all = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWidthPx = with(LocalDensity.current) { itemWidth.toPx() }
            val cardHeightPx = with(LocalDensity.current) { (itemWidth / 0.68f).toPx() }
            val gradientWidth = (0.4f * cardHeightPx)

            val infinityTransition = rememberInfiniteTransition()

            val xCardShimmer by infinityTransition.animateFloat(
                initialValue = 0f,
                targetValue = (cardWidthPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                        delayMillis = 300
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val yCardShimmer by infinityTransition.animateFloat(
                initialValue = 0f,
                targetValue = (cardHeightPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = LinearEasing,
                        delayMillis = 300
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val colors = listOf(
                MaterialTheme.colors.onBackground.copy(alpha = .4f),
                MaterialTheme.colors.onBackground.copy(alpha = .2f),
                MaterialTheme.colors.onBackground.copy(alpha = .4f)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                items(5) {
                    ShimmerProductItem(
                        colors = colors,
                        xShimmer = xCardShimmer,
                        yShimmer = yCardShimmer,
                        cardWidth = itemWidth,
                        gradientWidth = gradientWidth
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
public fun ShimmerPreview() {
    AppTheme {
        val colors = listOf(
            MaterialTheme.colors.onBackground.copy(alpha = .4f),
            MaterialTheme.colors.onBackground.copy(alpha = .2f),
            MaterialTheme.colors.onBackground.copy(alpha = .4f)
        )

        ShimmerProductItem(
            colors = colors,
            xShimmer = 0f,
            yShimmer = 0f,
            cardWidth = 160.dp,
            gradientWidth = 200f
        )
    }
}

@Preview
@Composable
public fun SectionPreview() {
    val dishes = listOf(
        UiDishItem(
            id = "44245434534534534",
            isFavorite = true,
            image = "",
            isSale = true,
            price = "234",
            title = "Булка городская"
        ),
        UiDishItem(
            id = "43455",
            isFavorite = true,
            image = "",
            isSale = true,
            price = "234",
            title = "Булка городская 1"
        ),
        UiDishItem(
            id = "44534534534",
            isFavorite = true,
            image = "",
            isSale = true,
            price = "234",
            title = "Булка городская 2"
        )
    )
    AppTheme {
        SectionProductCards(
            dishes = dishes,
            title = "Рекомендуем",
            onClick = {},
            onAddToCart = {},
            onToggleLike = {}
        )
    }
}
