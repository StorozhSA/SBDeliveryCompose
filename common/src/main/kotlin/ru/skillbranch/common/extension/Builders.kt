package ru.skillbranch.common.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

@Suppress("FunctionName")
public suspend fun <T> UnKillable(block: suspend CoroutineScope.() -> T): T =
    withContext(kotlinx.coroutines.NonCancellable) { block.invoke(this@withContext) }


