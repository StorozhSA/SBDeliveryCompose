package ru.skillbranch.sbdelivery.ui.screens.dish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.convertLongToTime
import ru.skillbranch.sbdelivery.data.domain.UiReviews
import java.util.*

@Composable
public fun Reviews(
    reviews: List<UiReviews>,
    modifier: Modifier = Modifier,
    rating: Float = 0f,
    onAddReview: () -> Unit
) {
    val lightBgColor = Color(android.graphics.Color.parseColor("#33313B"))
    Column(modifier = modifier.background(MaterialTheme.colors.surface)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Отзывы",
                color = MaterialTheme.colors.onPrimary,
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            if (rating > 0) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_baseline_star),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "$rating/5",
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { onAddReview() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = lightBgColor,
                    contentColor = Color.White
                ),
            ) {
                Text("Добавить отзыв")
            }
        }
        Column(modifier = Modifier.padding(8.dp)) {
            for (item in reviews) {
                ReviewItem(review = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
public fun ReviewItem(review: UiReviews, modifier: Modifier = Modifier) {
    val lightBgColor = Color(android.graphics.Color.parseColor("#33313B"))
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = lightBgColor,
        elevation = 2.dp
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val date = convertLongToTime(if (review.date.isBlank()) 0 else review.date.toLong())
                Text(
                    text = "${review.author}, $date",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                )

                Row {
                    for (i in 1..review.rating) {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_star),
                            contentDescription = null
                        )
                    }
                }
            }
            Text(
                fontSize = 12.sp,
                color = MaterialTheme.colors.onBackground,
                style = TextStyle(fontWeight = FontWeight.ExtraLight),
                text = review.text,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}

@Preview
@Composable
public fun ReviewsPreview() {
    val item1 = UiReviews(
        author = "Екатерина",
        date = Date().time.toString(),
        rating = 2,
        text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
        dishId = "",
        createdAt = 0,
        updatedAt = 0,
        id = "0",
        active = true
    )
    val item2 = UiReviews(
        author = "Петр",
        date = Date().time.toString(),
        rating = 2,
        text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
        dishId = "",
        createdAt = 0,
        updatedAt = 0,
        id = "0",
        active = true
    )
    AppTheme {
        Reviews(reviews = listOf(item1, item2), rating = 2f, onAddReview = {})
    }
}

@Preview
@Composable
public fun ReviewItemPreview() {
    val item1 = UiReviews(
        author = "Екатерина",
        date = Date().time.toString(),
        rating = 2,
        text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
        dishId = "",
        createdAt = 0,
        updatedAt = 0,
        id = "0",
        active = true
    )
    AppTheme {
        ReviewItem(
            review = item1
        )
    }
}
