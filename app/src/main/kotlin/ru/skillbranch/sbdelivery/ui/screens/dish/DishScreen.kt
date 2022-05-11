package ru.skillbranch.sbdelivery.ui.screens.dish

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.components.DishPrice
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.screens.DishUiState


@Composable
public fun DishScreen(vm: DishViewModel) {
    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    DishAssembled(
        vm.state.collectAsState().value,
        vm::mutate
    )
}


@OptIn(ExperimentalCoilApi::class)
@Composable
public fun DishAssembled(
    state: DishFeature.State,
    accept: (DishFeature.Msg) -> Unit
) {
    /*BackHandler {
        accept(DishFeature.Msg.EmptyDish)
    }*/

    ProgressBar(state.isLoading())

    when (state.payload) {
        is DishUiState.Empty -> {}
        is DishUiState.Loading -> {}
        is DishUiState.Error -> {}
        is DishUiState.Value -> {
            val dish = state.payload.dish

            ConstraintLayout(modifier = Modifier.verticalScroll(rememberScrollState())) {

                val (title, poster, like, action, description, price, addBtn, reviewBox) = createRefs()

                Image(
                    painter = rememberImagePainter(
                        data = dish.image,
                        builder = {
                            error(R.drawable.img_empty_place_holder)
                            placeholder(R.drawable.img_empty_place_holder)
                        }),
                    contentDescription = dish.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.44f)
                        .fillMaxSize()
                        .constrainAs(poster) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                dish.oldPrice?.let {
                    if (it > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .zIndex(1f)
                                .size(80.dp, 40.dp)
                                .background(
                                    color = MaterialTheme.colors.secondaryVariant,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .constrainAs(action) {
                                    top.linkTo(poster.top, margin = 10.dp)
                                    start.linkTo(poster.start, margin = 10.dp)
                                }
                        ) {
                            Text(
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.primaryVariant,
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                text = stringResource(R.string.label_action)

                            )
                        }
                    }
                }

                IconButton(
                    enabled = !state.isToggleLikeInProcess,
                    onClick = {
                        accept(
                            DishFeature.Msg.ToggleLike(
                                dishId = dish.id,
                                isFavorite = !dish.favorite
                            )
                        )
                    },
                    content = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            tint = (if (dish.favorite) {
                                MaterialTheme.colors.secondaryVariant
                            } else {
                                MaterialTheme.colors.onPrimary
                            }),
                            painter = painterResource(R.drawable.ic_favorite),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .zIndex(1f)
                        .constrainAs(like) {
                            top.linkTo(poster.top, margin = 10.dp)
                            end.linkTo(poster.end, margin = 10.dp)
                        }
                )

                Text(
                    fontSize = 24.sp,
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    text = dish.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(title) {
                            top.linkTo(poster.bottom, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.preferredWrapContent
                        }

                )
                Text(
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onBackground,
                    text = dish.description ?: "",
                    style = TextStyle(fontWeight = FontWeight.ExtraLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(description) {
                            top.linkTo(title.bottom, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.preferredWrapContent
                        }
                )


                DishPrice(price = dish.price, oldPrice = dish.oldPrice,
                    amount = state.amount,
                    onIncrement = { accept(DishFeature.Msg.Increment(dish.id)) },
                    onDecrement = { accept(DishFeature.Msg.Decrement(dish.id)) },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .constrainAs(price) {
                            top.linkTo(description.bottom, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })

                TextButton(
                    enabled = !state.isBasketAddInProcess && (state.amount != state.inCartAmount),
                    onClick = { accept(DishFeature.Msg.AddToBasket) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .constrainAs(addBtn) {
                            top.linkTo(price.bottom, margin = 32.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            bottom.linkTo(reviewBox.top, margin = 16.dp)
                            width = Dimension.preferredWrapContent
                        }
                ) {
                    if (state.amount != state.inCartAmount) {
                        Text(
                            text = "${stringResource(R.string.labelCartAdd)} ${if (state.amount > 0) "(${state.amount})" else ""}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Text(
                            text = "${stringResource(R.string.labelCartAdded)} (${state.inCartAmount})",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Если отзывы загружались отображаем или грузим сперва
                    Reviews(
                        reviews = state.reviews,
                        rating = state.rating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(reviewBox) {
                                top.linkTo(addBtn.bottom, margin = 32.dp)
                                start.linkTo(parent.start, margin = 16.dp)
                                end.linkTo(parent.end, margin = 16.dp)
                                bottom.linkTo(parent.bottom, margin = 16.dp)
                                width = Dimension.preferredWrapContent
                            }
                    ) { accept(DishFeature.Msg.ReviewDialogOpen(dishId = dish.id)) }
            }

            if (state.isReviewDialogShow) DishReviewDialog(dishId = dish.id, accept)
        }
    }
}


@SuppressLint("UnrememberedMutableState")

@Preview
@Composable
public fun ContentPreview() {
    val state = DishFeature.State(
        payload = DishUiState.Value(
            VDish(
                id = "0",
                image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
                name = "Бургер \"Америка\"",
                description = "320 г • Котлета из 100% говядины (прожарка medium) на гриле, картофельная булочка на гриле, фирменный соус, лист салата, томат, маринованный лук, жареный бекон, сыр чеддер.",
                oldPrice = 100,
                price = 200,
                updatedAt = 0,
                rating = 5.0,
                likes = 34,
                createdAt = 0,
                commentsCount = 23,
                category = "root",
                active = true,
                favorite = true
            )
        )
    )
    AppTheme {
        DishAssembled(state = state) {}
    }
}

