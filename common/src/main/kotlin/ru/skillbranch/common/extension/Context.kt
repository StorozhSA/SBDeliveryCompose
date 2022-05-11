package ru.skillbranch.common.extension

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import ru.skillbranch.common.network.retrofit2.RetrofitService

public fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics

    )
}

public fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

public fun Context.attrValue(@IdRes res: Int): Int {
    var value: Int? = null
    if (value == null) {
        val tv = TypedValue()
        if (this.theme.resolveAttribute(res, tv, true)) value = tv.data
        else throw Resources.NotFoundException("Resource with id $res not found")
    }
    return value
}

public fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

public fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

public fun Context.getString(resId: String): String {
    return try {
        getString(resources.getIdentifier(resId, "string", packageName))
    } catch (ex: Exception) {
        ""
    }
}

public fun Context.getString(resId: RetrofitService.ErrorItem): String = getString(resId.toString())
