package ru.skillbranch.sbdelivery.hilt

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class ResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    public fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }
}
