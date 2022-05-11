package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.domain.UiDishItem

@OptIn(ExperimentalCoilApi::class)
@Composable
public fun CardProduct(
    dish: UiDishItem,
    modifier: Modifier = Modifier,
    onToggleLike: (dishId: UiDishItem) -> Unit,
    onAddToCart: (dishId: UiDishItem) -> Unit,
    onClick: (dishId: UiDishItem) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = 160.dp)
            .clickable { onClick(dish) }
    ) {
        ConstraintLayout {
            val (fab, title, poster, price, sale, favorite) = createRefs()
            val painter = rememberImagePainter(
                data = dish.image,
                builder = {
                    crossfade(true)
                    scale(Scale.FILL)
                    placeholder(R.drawable.img_empty_place_holder)
                    error(R.drawable.img_empty_place_holder)
                }
            )

            Image(
                painter = painter,
                contentDescription = dish.title,
                contentScale = if (painter.state is ImagePainter.State.Success) ContentScale.Crop else ContentScale.Inside,
                modifier = Modifier
                    .constrainAs(poster) {
                        width = Dimension.fillToConstraints
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .aspectRatio(1f)
            )

            if (dish.isSale) {
                Text(
                    text = "АКЦИЯ",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .width(56.dp)
                        .background(
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                        )
                        .constrainAs(sale) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start)
                        }

                )
            }

            IconButton(
                onClick = { onToggleLike(dish) },
                modifier = Modifier.constrainAs(favorite) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    tint = if (dish.isFavorite) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.onPrimary,
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = "Toggle favorite"
                )
            }

            Text(text = "${dish.price} руб",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.constrainAs(price) {
                    width = Dimension.fillToConstraints
                    top.linkTo(poster.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                }
            )

            Text(
                text = dish.title,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 2,
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .constrainAs(title) {
                        width = Dimension.fillToConstraints
                        top.linkTo(price.bottom, margin = 8.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                    }
            )

            FloatingActionButton(
                onClick = { onAddToCart(dish) },
                modifier = Modifier
                    .requiredSize(40.dp)
                    .constrainAs(fab) {
                        top.linkTo(poster.bottom)
                        bottom.linkTo(poster.bottom)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_product),
                    contentDescription = "Add to cart",
                    tint = MaterialTheme.colors.onSecondary
                )

            }
        }
    }
}

@Preview
@Composable
public fun ProductCardPreview() {
    val item = UiDishItem(
        id = "44245434534534534",
        isFavorite = true,
        image = "",
        isSale = true,
        price = "234",
        title = "Булка городская"
    )
    AppTheme {
        CardProduct(
            dish = item,
            modifier = Modifier,
            onClick = {},
            onAddToCart = {},
            onToggleLike = {}
        )
    }
}
