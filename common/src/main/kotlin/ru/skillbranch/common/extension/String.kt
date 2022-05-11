package ru.skillbranch.common.extension

import android.text.TextUtils
import android.util.Patterns

public fun String?.randomString(length: Int): String {
    var localLength = this?.length ?: 1
    if (length in 1 until localLength) localLength = length
    return (1..localLength).map { this?.random() }.joinToString("")
}

public fun String?.randomString(): String {
    return (1..this?.length!!).map { this.random() }.joinToString("")
}

public fun String.hasDigits(): Boolean {
    return this.matches(".*\\d+.*".toRegex())
}

public fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
