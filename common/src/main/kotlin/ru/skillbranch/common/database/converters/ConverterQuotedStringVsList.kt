package ru.skillbranch.common.database.converters

import androidx.room.TypeConverter

public class ConverterQuotedStringVsList : ITypeConverter<List<String>, String> {

    @TypeConverter
    override fun marshalling(value: String): List<String> = value.split(",")

    @TypeConverter
    override fun unmarshalling(value: List<String>): String = value.joinToString(",")
}
