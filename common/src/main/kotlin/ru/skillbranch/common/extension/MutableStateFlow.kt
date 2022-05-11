package ru.skillbranch.common.extension

import kotlinx.coroutines.flow.MutableStateFlow

public fun <T> MutableStateFlow<T>.update(upd: (currentState: T) -> T) {
    value = upd(value)
}
