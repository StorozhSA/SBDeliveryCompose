package ru.skillbranch.common.elm

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.common.extension.tickerFlow
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

public class HandlerJobsController(private val ownerClassName: String = "") {

    private val jobs: ConcurrentHashMap<String, Job> = ConcurrentHashMap()

    public fun add(name: String, job: Job) {
        logd("Added job = $name $job")
        jobs[name] = job
    }

    public fun cancelHandlerJobs() {
        logd("Invoke cancelHandlerJobs")
        jobs.forEach {
            logd("invoke cancel for job: ${it.key}")
            it.value.cancel()
        }
        jobs.clear()
    }

    init {
        logd("Inited HandlerJobsController")

        // Periodic timer for clean completed jobs
        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            tickerFlow(
                delay = 5.seconds,
                period = 5.seconds
            ).collectLatest {
                jobs.forEach {
                    if (!it.value.isActive) jobs.remove(it.key)
                }
                if (jobs.isNotEmpty()) logd("is active jobs = ${jobs.size} ${jobs.keys}")
            }
        }
    }

    private fun logd(str: String) {
        Log.d(ownerClassName + " -> " + javaClass.simpleName, str)
    }
}

