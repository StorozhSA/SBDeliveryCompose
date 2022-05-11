package ru.skillbranch.sbdelivery.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.components.SectionProductCards
import ru.skillbranch.sbdelivery.ui.components.ShimmerSection
import ru.skillbranch.sbdelivery.ui.screens.DishesUiState.*
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature


@Composable
public fun HomeScreen(vm: HomeViewModel) {

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    val state = vm.state.collectAsState().value

    // Smoothly scroll 100px on first composition
    val scrollState = rememberScrollState()
    /*LaunchedEffect(Unit) { scrollState.animateScrollTo(value =100, animationSpec  = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessMedium
    )) }*/

    ProgressBar(state.popular == Loading || state.recommended == Loading || state.best == Loading || state.isLoading())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        // Wallpaper banner
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wallpaper),
                contentDescription = "Wallpaper",
                contentScale = ContentScale.Crop
            )
        }

        // Recommended
        when (state.recommended) {
            Empty -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Рекомендуем"
                )
            }
            Error -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Рекомендуем"
                )
            }
            Loading -> ShimmerSection(
                itemWidth = 160.dp,
                title = "Рекомендуем"
            )
            is Value -> SectionProductCards(
                dishes = state.recommended.dishes.toList(),
                title = "Рекомендуем",
                onClick = { vm.mutate(HomeFeature.Msg.NavigateToDish(DishFeature.target(it.id))) },
                onAddToCart = { vm.mutate(HomeFeature.Msg.AddToBasket(it.id)) },
                onToggleLike = { vm.mutate(HomeFeature.Msg.ToggleLike(it.id, !it.isFavorite)) }
            )
            is PagingValue -> {}
        }

        // Best
        when (state.best) {
            Empty -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Лучшее"
                )
            }
            Error -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Лучшее"
                )
            }
            Loading -> ShimmerSection(
                itemWidth = 160.dp,
                title = "Лучшее"
            )
            is Value -> SectionProductCards(
                dishes = state.best.dishes.toList(),
                title = "Лучшее",
                onClick = { vm.mutate(HomeFeature.Msg.NavigateToDish(DishFeature.target(it.id))) },
                onAddToCart = { vm.mutate(HomeFeature.Msg.AddToBasket(it.id)) },
                onToggleLike = { vm.mutate(HomeFeature.Msg.ToggleLike(it.id, !it.isFavorite)) }
            )
            is PagingValue -> {}
        }

        // Popular
        when (state.popular) {
            Empty -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Популярное"
                )
            }
            Error -> {
                ShimmerSection(
                    itemWidth = 160.dp,
                    title = "Популярное"
                )
            }
            Loading -> ShimmerSection(
                itemWidth = 160.dp,
                title = "Популярное"
            )
            is Value -> SectionProductCards(
                dishes = state.popular.dishes.toList(),
                title = "Популярное",
                onClick = { vm.mutate(HomeFeature.Msg.NavigateToDish(DishFeature.target(it.id))) },
                onAddToCart = { vm.mutate(HomeFeature.Msg.AddToBasket(it.id)) },
                onToggleLike = { vm.mutate(HomeFeature.Msg.ToggleLike(it.id, !it.isFavorite)) }
            )
            is PagingValue -> {}
        }
    }
}
