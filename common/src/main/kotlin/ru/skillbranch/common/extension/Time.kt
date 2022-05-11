package ru.skillbranch.common.extension

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

public fun tickerFlow(period: Duration, delay: Duration = Duration.ZERO): Flow<Unit> = flow {
    delay(delay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}
