package ru.skillbranch.common.extension

public fun Boolean?.isTrue(block: () -> Unit) {
    if (this == true) block.invoke()
}
