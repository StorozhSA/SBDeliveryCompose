package ru.skillbranch.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.extension.logi
import ru.skillbranch.common.extension.manage

public interface ISharedPreferences {
    public val store: SharedPreferences
    public val changesListener: MutableSet<(key: String, value: Any) -> Unit>
    public fun migratePreferences()
}

public abstract class ASharedPreferences(ctx: Context) : ISharedPreferences {

    final override val store: SharedPreferences by lazy {
        ctx.getSharedPreferences(ctx.packageName, MODE_PRIVATE)
    }

    final override val changesListener: MutableSet<(key: String, value: Any) -> Unit> =
        mutableSetOf()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        val value = when (store.all[k]) {
            is Int -> store.getInt(k, 0)
            is Long -> store.getLong(k, 0)
            is Float -> store.getFloat(k, 0f)
            is String -> store.getString(k, "")
            is Boolean -> store.getBoolean(k, false)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
        logd("SharedPreferences changed key = $k value=$value")
        changesListener.forEach { it.invoke(k, value!!) }
    }

    // Version control of settings
    private var oldVersion: Int by store.manage(PREFERENCES_VERSION)

    init {
        if (oldVersion < PREFERENCES_VERSION) migrate()
        store.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun migrate() {
        logi("migratePreferences() invoke. Settings migration process started.")
        // //////// Migration procedures. Start ///////////
        migratePreferences()
        // //////// Migration procedures. End ///////////
        oldVersion = PREFERENCES_VERSION
        logi("migratePreferences() invoke. Settings migration process end.")
    }

    public companion object {
        public const val PREFERENCES_VERSION: Int = 1
    }

    public abstract override fun migratePreferences()
}
