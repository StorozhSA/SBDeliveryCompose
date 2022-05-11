package ru.skillbranch.common.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

public fun <T> MediatorLiveData<T>.update(upd: (currentState: T) -> T) {
    value = upd(value!!)
}

/***
 * функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
 * лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
 * изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
 *
 * Пример использования:
 * subscribeOnDataSource(getDataFromDataBaseOrNet()) { info, state ->
 *      info?.let {
 *          state.copy(
 *              isBookmark = it.isBookmark,
 *              isLike = it.isLike
 *          )
 *      }
 *  }
 */
public fun <S, T> MediatorLiveData<T>.subscribeStateToDataSource(
    source: LiveData<S>,
    onChanged: (info: S, state: T) -> T
) {
    addSource(source) { value = onChanged(it, this.value!!) ?: return@addSource }
}
