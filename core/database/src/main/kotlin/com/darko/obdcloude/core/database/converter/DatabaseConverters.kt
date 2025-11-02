package com.darko.obdcloude.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return ""
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromEcuType(value: EcuType): String {
        return value.name
    }

    @TypeConverter
    fun toEcuType(value: String): EcuType {
        return EcuType.valueOf(value)
    }
}