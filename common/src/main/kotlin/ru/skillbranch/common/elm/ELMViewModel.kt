package ru.skillbranch.common.elm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.skillbranch.common.IFlowPoints
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.common.extension.mutableStateFlow


public abstract class ELMViewModel<S : IState, M : IMessage, EF : IEffect>(
    initialState: S,
    private val handler: CoroutineExceptionHandler,
    @Suppress("UNUSED_PARAMETER") points: IFlowPoints,
    savedStateHandle: SavedStateHandle
) : ViewModel(), IELMViewModel<S, EF, M> {

    private val handlerJobs: HandlerJobsController = HandlerJobsController(javaClass.simpleName)

    private val _state: MutableStateFlow<S> by savedStateHandle.mutableStateFlow(
        initValue = initialState,
        scope = viewModelScope
    )

    public val state: StateFlow<S> = _state.asStateFlow()

    protected fun state(): S = state.value

    protected fun emptyPairStub(): Pair<S, Set<EF>> = Pair(state.value, emptySet())

    protected var jobOnStart: Job? = null

    init {
        Log.d(this.javaClass.simpleName, "Inited ViewModel")
    }

    // Обработка мутаций
    public override fun mutate(msg: M) {
        msg.let {
            logd("MUTATION -> ${it.javaClass.simpleName} - $it")
            reduce(it).let { (first, second) ->
                _state.value = first

                viewModelScope.launch(Dispatchers.IO + SupervisorJob() + handler) {
                    logd("REDUCE working in thread ${Thread.currentThread().name}")
                    second.forEach { eff ->
                        launch(Dispatchers.IO) {
                            handle(eff)
                            logd("EFFECT -> ${eff.javaClass.simpleName} - $eff")
                            logd("EFFECT working in thread ${Thread.currentThread().name}")
                        }.let { job -> handlerJobs.add(eff.javaClass.simpleName, job) }
                    }
                }
            }
        }
    }

    public abstract override suspend fun handle(effect: EF)
    public abstract override fun reduce(msg: M): Pair<S, Set<EF>>

    public override fun cancelAllJobs() {
        logd("Invoke cancelAllJobs")
        viewModelScope.coroutineContext.cancelChildren()
        handlerJobs.cancelHandlerJobs()
    }

    public override fun cancelHandlerJobs() {
        handlerJobs.cancelHandlerJobs()
    }

    public override fun onStart() {
        logd("onStart")
        jobOnStart?.cancel()
    }

    public override fun onStop() {
        logd("onStop")
        jobOnStart?.cancel()
        cancelHandlerJobs()
    }
}
