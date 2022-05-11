package ru.skillbranch.common.extension

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T> SharedPreferences.manage(
    initValue: T,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        logd("manage() get property ${key(property)}")
        return if (check(key(property), this@manage)) {
            read(key(property), initValue, this@manage)
        } else {
            initValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        logd("manage() set property ${key(property)}")
        write(key(property), value, this@manage)
    }
}

public fun <T> SharedPreferences.mutableLiveData(
    initValue: T,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, MutableLiveData<T>> =
    object : ReadWriteProperty<Any, MutableLiveData<T>> {
        private lateinit var mld: MutableLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): MutableLiveData<T> {
            initialize(key(property), initValue)
            return mld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: MutableLiveData<T>) {
            initialize(key(property), value.value ?: initValue)
        }

        private fun initialize(k: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = MutableLiveData(read(k, value, this@mutableLiveData))
                // Subscribe to changes
                mld.observeForever {
                    write(k, it, this@mutableLiveData)
                }
            }
        }
    }

private fun <T> write(
    key: String,
    value: T,
    shp: SharedPreferences
): T {
    shp.edit().apply {
        when (value) {
            is Boolean -> putBoolean(key, value as Boolean)
            is String -> putString(key, value as String)
            is Float -> putFloat(key, value as Float)
            is Int -> putInt(key, value as Int)
            is Long -> putLong(key, value as Long)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }.apply()
        logd("Saved value $key = $value to SharedPreferences")
    }
    return value
}

private fun <T> read(
    key: String,
    initValue: T,
    shp: SharedPreferences
): T {
    return shp.run {
        @Suppress("UNCHECKED_CAST")
        val value = when (initValue) {
            is Int -> getInt(key, initValue) as T
            is Long -> getLong(key, initValue) as T
            is Float -> getFloat(key, initValue) as T
            is String -> getString(key, initValue) as T
            is Boolean -> getBoolean(key, initValue) as T
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
        logd("Get value $key = $value from SharedPreferences")
        value ?: initValue
    }
}

private fun check(key: String, shp: SharedPreferences): Boolean = shp.contains(key)
