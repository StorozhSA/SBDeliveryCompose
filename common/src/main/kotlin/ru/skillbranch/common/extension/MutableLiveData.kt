package ru.skillbranch.common.extension

import androidx.lifecycle.MutableLiveData

public fun <T> MutableLiveData<T>.update(upd: (currentState: T) -> T) {
    value = upd(value!!)
}
