package ru.skillbranch.common.extension

public interface EnumBits<N : Number> {
    public val offset: N
}

public inline fun <reified T> Set<T>.encodeToBitsByOffset(): Int where T : Enum<T>, T : EnumBits<Int> {
    var res = 0
    require(enumValues<T>().size < 33) { "Index out of range. Use encodeToBitsByOrdinal64." }
    forEach { res = res or it.offset }
    return res
}

public inline fun <reified T> Set<T>.encodeToBitsByOffset(): Long where T : Enum<T>, T : EnumBits<Long> {
    var res: Long = 0
    require(enumValues<T>().size < 65) { "Index out of range." }
    forEach { res = res or it.offset }
    return res
}

public inline fun <reified T : Enum<T>> Set<T>.encodeToBitsByOrdinal32(): Int {
    var res = 0
    require(enumValues<T>().size < 33) { "Index out of range. Use encodeToBitsByOrdinal64." }
    forEach { res = res or 1.shl(it.ordinal) }
    return res
}

public inline fun <reified T : Enum<T>> Set<T>.encodeToBitsByOrdinal64(): Long {
    var res: Long = 0
    require(enumValues<T>().size < 65) { "Index out of range." }
    forEach { res = res or 1L.shl(it.ordinal) }
    return res
}

public inline fun <reified T> Int.decodeFromBitsByOffset(): Set<T> where T : Enum<T>, T : EnumBits<Int> =
    mutableSetOf<T>()
        .apply {
            enumValues<T>().forEach {
                if (((this@decodeFromBitsByOffset or 0) and it.offset) == it.offset) {
                    this@apply.add(it)
                }
            }
            return this
        }

public inline fun <reified T : Enum<T>> Int.decodeFromBitsByOrdinal(): Set<T> =
    mutableSetOf<T>()
        .apply {
            enumValues<T>().forEach {
                if (((this@decodeFromBitsByOrdinal or 0) and 1.shl(it.ordinal)) == 1.shl(it.ordinal)) {
                    this@apply.add(it)
                }
            }
            return this
        }


public inline fun <reified T> Long.decodeFromBitsByOffset(): Set<T> where T : Enum<T>, T : EnumBits<Long> =
    mutableSetOf<T>()
        .apply {
            enumValues<T>().forEach {
                if (((this@decodeFromBitsByOffset or 0) and it.offset) == it.offset) {
                    this@apply.add(it)
                }
            }
            return this
        }

public inline fun <reified T : Enum<T>> Long.decodeFromBitsByOrdinal(): Set<T> =
    mutableSetOf<T>()
        .apply {
            enumValues<T>().forEach {
                if (((this@decodeFromBitsByOrdinal or 0) and 1L.shl(it.ordinal)) == 1L.shl(it.ordinal)) {
                    this@apply.add(it)
                }
            }
            return this
        }
