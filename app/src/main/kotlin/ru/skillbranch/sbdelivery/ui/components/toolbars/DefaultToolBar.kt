package ru.skillbranch.sbdelivery.ui.components.toolbars

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.common.SortBy
import ru.skillbranch.sbdelivery.ui.components.CartButton
import ru.skillbranch.sbdelivery.ui.components.SortButton

@Composable
public fun DefaultToolBar(
    title: String,
    cartCount: Int,
    canCart: Boolean = true,
    canBack: Boolean = false,
    canSort: Boolean = false,
    sortBy: SortBy = SortBy.Alphabetically,
    sortOrder: Boolean = true,
    onClickMenuByAlphabetically: () -> Unit = {},
    onClickMenuByPopularity: () -> Unit = {},
    onClickMenuByRating: () -> Unit = {},
    onClickSort: () -> Unit,
    onClickCart: () -> Unit,
    onDrawer: () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            if (!canBack) {
                IconButton(
                    onClick = onDrawer,
                    content = {
                        Icon(
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_menu_24),
                            contentDescription = "Home"
                        )
                    }
                )
            } else {
                IconButton(
                    onClick = { dispatcher.onBackPressed() },
                    content = {
                        Icon(
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                )
            }

        },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            if (canSort) {
                SortButton(sortOrder = sortOrder) {
                    expanded = !expanded
                    onClickSort()
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onClickMenuByAlphabetically.invoke()
                        expanded = false
                    },
                    modifier = Modifier.then(
                        if (sortBy == SortBy.Alphabetically) Modifier.background(MaterialTheme.colors.secondary) else Modifier
                    )
                ) {
                    Row {
                        if (sortOrder) {
                            Icon(
                                tint = if (sortBy == SortBy.Alphabetically) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_a_z),
                                contentDescription = "Sort by Alphabetically down",
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                tint = if (sortBy == SortBy.Alphabetically) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_z_a),
                                contentDescription = "Sort by Alphabetically up",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(3.dp))
                        Text(stringResource(R.string.ByAlphabetically))
                    }

                }
                //Divider()
                DropdownMenuItem(
                    onClick = {
                        onClickMenuByPopularity.invoke()
                        expanded = false
                    },
                    modifier = Modifier.then(
                        if (sortBy == SortBy.Popularity) Modifier.background(MaterialTheme.colors.secondary) else Modifier
                    )
                ) {
                    Row {
                        if (sortOrder) {
                            Icon(
                                tint = if (sortBy == SortBy.Popularity) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_a_z),
                                contentDescription = "Sort by Popularity down",
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                tint = if (sortBy == SortBy.Popularity) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_z_a),
                                contentDescription = "Sort by Popularity up",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(3.dp))
                        Text(stringResource(R.string.ByPopularity))
                    }
                }
                //Divider()
                DropdownMenuItem(
                    onClick = {
                        onClickMenuByRating.invoke()
                        expanded = false
                    },
                    modifier = Modifier.then(
                        if (sortBy == SortBy.Rating) Modifier.background(MaterialTheme.colors.secondary) else Modifier
                    )
                ) {
                    Row {
                        if (sortOrder) {
                            Icon(
                                tint = if (sortBy == SortBy.Rating) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_a_z),
                                contentDescription = "Sort by Rating down",
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                tint = if (sortBy == SortBy.Rating) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
                                painter = painterResource(R.drawable.ic_sort_z_a),
                                contentDescription = "Sort by Rating up",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(3.dp))
                        Text(stringResource(R.string.ByRating))
                    }
                }
            }
            if (canCart) {
                CartButton(cartCount = cartCount, onClickCart = onClickCart)
            }
        }
    )
}

@Preview
@Composable
public fun CanBackDefaultToolbarPreview() {
    AppTheme {
        DefaultToolBar(
            title = "Can back",
            cartCount = 10,
            canBack = true,
            canSort = true,
            onClickSort = {},
            onClickCart = {},
            onDrawer = {}
        )
    }
}

@Preview
@Composable
public fun NotCanBackDefaultToolbarPreview() {
    AppTheme {
        DefaultToolBar(
            title = "Can back",
            cartCount = 10,
            canBack = false,
            canSort = true,
            onClickSort = {},
            onClickCart = {},
            onDrawer = {}
        )
    }
}

@Preview
@Composable
public fun DefaultToolbarPreview() {
    AppTheme {
        DefaultToolBar(
            title = "Can back",
            cartCount = 10,
            canBack = false,
            canSort = true,
            onClickSort = {},
            onClickCart = {},
            onDrawer = {}
        )
    }
}

@Preview
@Composable
public fun DefaultToolbarPreview1() {
    AppTheme {
        DefaultToolBar(
            title = "Can back",
            cartCount = 10,
            canBack = false,
            canSort = true,
            onClickSort = {},
            onClickCart = {},
            onDrawer = {}
        )
    }
}
