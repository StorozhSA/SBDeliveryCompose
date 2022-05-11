package ru.skillbranch.common.database.converters

public interface ITypeConverter<A, B> {
    public fun marshalling(value: B): A
    public fun unmarshalling(value: A): B
}
