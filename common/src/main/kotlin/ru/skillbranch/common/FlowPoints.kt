package ru.skillbranch.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

public interface IFlowPoints {

    public val eventProgressBar: IPointShared<Boolean>
    public val eventNotify: IPointShared<Notify>
    public val eventNav: IPointShared<Nav>
    public val stateNav: IPointState<Nav>

    // Progress bar
    public fun progressBar(v: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            eventProgressBar.m().emit(value = v)
        }
    }

    // Notification
    public fun notify(n: Notify) {
        CoroutineScope(Dispatchers.Default).launch {
            eventNotify.m().emit(value = n)
        }
    }

    // Navigate
    public fun navigate(n: Nav) {
        CoroutineScope(Dispatchers.Default).launch {
            eventNav.m().emit(value = n)
        }
    }

    public interface IPoint<T>

    public interface IPointShared<T> : IPoint<T> {
        public fun m(): MutableSharedFlow<T>
        public fun i(): SharedFlow<T> = m().asSharedFlow()
    }

    public interface IPointState<T> : IPoint<T> {
        public fun m(): MutableStateFlow<T>
        public fun i(): StateFlow<T> = m().asStateFlow()
    }

    public open class PointShared<T>(private val flow: MutableSharedFlow<T>) : IPointShared<T> {
        override fun m(): MutableSharedFlow<T> = flow
    }

    public open class PointState<T>(private val flow: MutableStateFlow<T>) : IPointState<T> {
        override fun m(): MutableStateFlow<T> = flow
    }
}
