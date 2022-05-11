package ru.skillbranch.sbdelivery.common

import java.io.Serializable

public sealed interface SortBy : Serializable {
    public object Alphabetically : SortBy
    public object Popularity : SortBy
    public object Rating : SortBy
}
