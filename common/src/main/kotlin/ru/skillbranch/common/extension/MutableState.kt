package ru.skillbranch.common.extension

import androidx.compose.runtime.MutableState

public fun <T> MutableState<T>.update(upd: (currentState: T) -> T) {
    value = upd(value)
}
