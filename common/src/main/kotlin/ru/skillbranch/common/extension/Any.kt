package ru.skillbranch.common.extension

import android.util.Log

public fun Any?.isNull(): Boolean {
    return this == null
}

public fun Any?.isNotNull(): Boolean {
    return this != null
}

@Suppress("unused")
public inline val <reified T> T.TAG: String
    get() = T::class.java.name

public inline fun <reified T> T.logv(message: String): Int = Log.v(TAG, message)
public inline fun <reified T> T.logi(message: String): Int = Log.i(TAG, message)
public inline fun <reified T> T.logw(message: String): Int = Log.w(TAG, message)
public inline fun <reified T> T.logd(message: String): Int = Log.d(TAG, message)
public inline fun <reified T> T.loge(message: String): Int = Log.e(TAG, message)

public fun isAllTrue(vararg elements: Boolean): Boolean {
    val elms = if (elements.isNotEmpty()) elements.toSet() else emptySet()
    return !elms.contains(false)
}

public fun Int.decZero(): Int = if (this.dec() > 0) this.dec() else 0


