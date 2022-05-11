package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ru.skillbranch.common.extension.logd
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.data.domain.UiDishItem
import ru.skillbranch.sbdelivery.models.database.domains.IId

@Composable
public fun <T> Grid(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    val chunkedList = items.chunked(cols)
    Column(
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        modifier = modifier.padding(contentPadding)
    ) {
        chunkedList.forEach { chunk ->
            Row {
                repeat(chunk.size) {
                    Box(modifier = Modifier.weight(1f / cols)) {
                        itemContent(chunk[it])
                    }
                    if (it < chunk.size.dec()) Spacer(modifier = Modifier.width(cellsPadding))
                }

                val emptyCols = cols - chunk.size
                if (emptyCols > 0) {
                    repeat(emptyCols) {
                        Spacer(modifier = Modifier.width(cellsPadding))
                        Spacer(modifier = Modifier.weight(1f / cols))
                    }
                }
            }
        }
    }
}

/*@Composable
public fun <T> LazyGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    val chunkedList = items.chunked(cols)
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        modifier = modifier.padding(contentPadding)
    ) {
        items(chunkedList) { chunk ->
            Row {
                repeat(chunk.size) {
                    Box(modifier = Modifier.weight(1f / cols)) {
                        itemContent(chunk[it])
                    }
                    if (it < chunk.size.dec()) Spacer(modifier = Modifier.width(cellsPadding))
                }

                val emptyCols = cols - chunk.size
                if (emptyCols > 0) {
                    repeat(emptyCols) {
                        Spacer(modifier = Modifier.width(cellsPadding))
                        Spacer(modifier = Modifier.weight(1f / cols))
                    }
                }
            }
        }
    }
}*/


@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun <T> LazyGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        cells = if (cols == 0) GridCells.Adaptive(160.dp) else GridCells.Fixed(cols),
        content = {
            items(items.size) { index ->
                items[index]?.let {
                    Box(modifier = modifier.padding(start = cellsPadding)) {
                        itemContent(it)
                    }
                }
            }
        })
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
public fun <S, T : IId<S>> LazyGridSwipe(
    items: List<T>,
    modifier: Modifier = Modifier,
    cols: Int = 2,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    cellsPadding: Dp = 0.dp,
    itemSwipe: ((T) -> Unit) = {},
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        cells = if (cols == 0) GridCells.Adaptive(160.dp) else GridCells.Fixed(cols),
        content = {
            items(items) { item ->
                key(item.id) {
                    var unread by remember { mutableStateOf(false) }
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissedToEnd) unread = !unread
                            it != DismissedToEnd
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier.padding(vertical = 4.dp),
                        directions = setOf(StartToEnd, EndToStart),
                        dismissThresholds = { direction -> FractionalThreshold(if (direction == StartToEnd) 0.25f else 0.5f) },
                        background = {
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    Default -> Color.LightGray
                                    DismissedToEnd -> Color.Green
                                    DismissedToStart -> Color.Red
                                }
                            )

                            val alignment = when (direction) {
                                StartToEnd -> CenterStart
                                EndToStart -> CenterEnd
                            }

                            val icon = when (direction) {
                                StartToEnd -> Icons.Default.Done
                                EndToStart -> Icons.Default.Delete
                            }

                            val scale by animateFloatAsState(if (dismissState.targetValue == Default) 0.75f else 1f)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color),
                                //.padding(cellsPadding),
                                contentAlignment = alignment
                            ) {
                                IconButton(
                                    onClick = { itemSwipe.invoke(item) },
                                    content = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "Localized description",
                                            modifier = Modifier.scale(scale)
                                        )
                                    }
                                )
                            }
                            if (dismissState.currentValue != Default) itemSwipe.invoke(item)
                        },
                        dismissContent = {
                            Box {
                                itemContent(item)
                            }
                        }
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun <T : Any> LazyGridPaged(
    items: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    cols: Int = 0,
    contentPadding: PaddingValues = PaddingValues(
        start = 8.dp,
        top = 8.dp,
        bottom = 8.dp,
        end = 16.dp
    ),
    cellsPadding: Dp = 8.dp,
    itemContent: @Composable BoxScope.(T) -> Unit
) {
    items.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                logd(" ---- You can add modifier to manage load state when first time response page is loading")
            }
            loadState.append is LoadState.Loading -> {
                logd(" ---- You can add modifier to manage load state when next response page is loading")
            }
            loadState.append is LoadState.Error -> {
                logd(" ---- You can use modifier to show error message")
            }
        }
    }

    LazyVerticalGrid(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(cellsPadding),
        cells = if (cols == 0) GridCells.Adaptive(160.dp) else GridCells.Fixed(cols),
        content = {
            items(items.itemCount) { index ->
                items[index]?.let {
                    Box(modifier = modifier.padding(start = cellsPadding)) {
                        itemContent(it)
                    }
                }
            }
        }
    )
}

@Preview
@Composable
public fun GridPreview() {
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
        Grid(items = dishes, cols = 2) {
            CardProduct(dish = it, onToggleLike = {}, onAddToCart = {}, onClick = {})
        }
    }
}
