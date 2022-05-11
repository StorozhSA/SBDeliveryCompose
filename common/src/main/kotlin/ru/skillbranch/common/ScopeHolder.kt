package ru.skillbranch.common

import kotlinx.coroutines.*

public interface IScopeHolder {
    public val appScope: CoroutineScope
    public fun cancel()
}

public abstract class AbsScopeHolder : IScopeHolder {
    private val job: Job = SupervisorJob()
    override val appScope: CoroutineScope =
        CoroutineScope(Dispatchers.Default + job + CoroutineName("appScope"))

    override fun cancel() {
        job.cancelChildren()
    }
}
