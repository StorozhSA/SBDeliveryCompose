package ru.skillbranch.sbdelivery.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R

@Composable
public fun SortButton(
    sortOrder: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        content = {
            if (sortOrder) {
                Icon(
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_sort_down_alt),
                    contentDescription = "Sort down"
                )
            } else {
                Icon(
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_sort_up_alt),
                    contentDescription = "Sort up"
                )
            }
        }
    )
}

@Preview
@Composable
public fun SortButtonEmptyPreview() {
    AppTheme {
        SortButton {}
    }
}
