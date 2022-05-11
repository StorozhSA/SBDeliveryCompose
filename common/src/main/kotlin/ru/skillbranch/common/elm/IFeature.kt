package ru.skillbranch.common.elm

import android.util.Log
import ru.skillbranch.common.elm.IFeature.*
import java.io.Serializable

/**
 * Feature interface
 */
public interface IFeature<S : IState, M : IMessage, EF : IEffect> {
    public interface IMessage
    public interface IEffect
    public interface IELMViewModel<S, EF, M> {
        public fun mutate(msg: M)
        public fun reduce(msg: M): Pair<S, Set<EF>>
        public suspend fun handle(effect: EF)
        public fun onStart()
        public fun onStop()
        public fun cancelAllJobs()
        public fun cancelHandlerJobs()
        public fun logd(str: String) {
            Log.d(javaClass.simpleName, str)
        }
    }

    public val target: String

    public interface IState : Serializable {
        public val isLoading: Int
        public fun isLoading(): Boolean = isLoading > 0

        public companion object {
            private const val serialVersionUID = 21110000000001L
        }
    }
}
