package ru.skillbranch.sbdelivery.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem
import java.util.*



@Composable
public fun TabsBar(
    tabs: List<UiCategoryItem>,
    startTab: Int = 0,
    onSelected: (tab: UiCategoryItem, index: Int) -> Unit
) {
    if (tabs.isNotEmpty()) {
        var selectedId = if (tabs.size > startTab) tabs[startTab].id else tabs[0].id
        val tabsScrollState = rememberScrollState()

        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(
                    state = tabsScrollState,
                    orientation = Orientation.Horizontal,
                    enabled = true
                )
        ) {
            itemsIndexed(tabs) { index, item ->
                Tab(
                    title = item.name,
                    selected = (item.id == selectedId),
                    onClick = {
                        selectedId = item.id
                        onSelected.invoke(item, index)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
public fun Tab(
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary

    Surface(
        onClick = onClick,
        color = bgColor,
        border = BorderStroke(1.dp, bgColor),
        shape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            modifier = modifier.padding(12.dp)
        )
    }
}


@Composable
@Preview
public fun PreviewOneTabSelected() {
    AppTheme {
        Tab(title = "TAB1", selected = true) {}
    }
}


@Composable
@Preview
public fun PreviewOneTabTrue() {
    AppTheme {
        Tab(title = "TAB1", selected = false) {}
    }
}


@Composable
@Preview
public fun PreviewTabs() {
    AppTheme {
        TabsBar(
            LinkedList<UiCategoryItem>().apply {
                add(UiCategoryItem("1", "TAB1", null, 0, "root", false))
                add(UiCategoryItem("2", "TAB2", null, 0, "root", false))
                add(UiCategoryItem("3", "TAB3", null, 0, "root", false))
                add(UiCategoryItem("4", "TAB4", null, 0, "root", false))
                add(UiCategoryItem("5", "TAB1", null, 0, "root", false))
                add(UiCategoryItem("6", "TAB2", null, 0, "root", false))
                add(UiCategoryItem("7", "TAB3", null, 0, "root", false))
                add(UiCategoryItem("8", "TAB4", null, 0, "root", false))
                add(UiCategoryItem("9", "TAB1", null, 0, "root", false))
                add(UiCategoryItem("10", "TAB2", null, 0, "root", false))
                add(UiCategoryItem("11", "TAB3", null, 0, "root", false))
                add(UiCategoryItem("12", "TAB4", null, 0, "root", false))
                add(UiCategoryItem("13", "TAB1", null, 0, "root", false))
                add(UiCategoryItem("14", "TAB2", null, 0, "root", false))
                add(UiCategoryItem("15", "TAB3", null, 0, "root", false))
                add(UiCategoryItem("16", "TAB4", null, 0, "root", false))
                add(UiCategoryItem("17", "TAB1", null, 0, "root", false))
                add(UiCategoryItem("18", "TAB2", null, 0, "root", false))
                add(UiCategoryItem("19", "TAB3", null, 0, "root", false))
                add(UiCategoryItem("20", "TAB4", null, 0, "root", false))
            },
            0
        ) { item, _ ->
            Log.d("PREVIEW", "Selected ${item.name}")
        }
    }
}
