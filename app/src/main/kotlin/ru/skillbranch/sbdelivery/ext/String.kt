package ru.skillbranch.sbdelivery.ext

public fun String.base(): String = this.split("/", "?")[0]
