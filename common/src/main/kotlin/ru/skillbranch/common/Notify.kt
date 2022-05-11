package ru.skillbranch.common

import androidx.annotation.StringRes

public sealed class Notify {
    public data class Text(val message: String) : Notify()
    public data class LinkStr(val resName: String) : Notify()
    public data class LinkInt(@StringRes val resId: Int) : Notify()
    public data class Action(
        val actionLabel: String,
        val handler: ((actionLabel: String) -> Unit) = { _ -> }
    ) : Notify()

    public data class Error(
        val message: String,
        val appCode: String,
        val handler: ((message: String, appCode: String) -> Unit) = { _, _ -> }
    ) : Notify()
}
