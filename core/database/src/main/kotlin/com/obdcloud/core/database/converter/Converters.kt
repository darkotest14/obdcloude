package com.obdcloud.core.database.converter

import androidx.room.TypeConverter
import com.obdcloud.core.domain.model.Protocol
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromProtocolList(protocols: List<Protocol>): String {
        return gson.toJson(protocols)
    }
    
    @TypeConverter
    fun toProtocolList(data: String): List<Protocol> {
        val listType = object : TypeToken<List<Protocol>>() {}.type
        return gson.fromJson(data, listType)
    }
    
    @TypeConverter
    fun fromStringList(strings: List<String>): String {
        return gson.toJson(strings)
    }
    
    @TypeConverter
    fun toStringList(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
    
    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String? {
        if (map == null) return null
        return gson.toJson(map)
    }
    
    @TypeConverter
    fun toStringMap(data: String?): Map<String, String>? {
        if (data == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(data, mapType)
    }
}