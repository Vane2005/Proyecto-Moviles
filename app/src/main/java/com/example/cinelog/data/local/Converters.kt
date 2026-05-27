package com.example.cinelog.data.local

import androidx.room.TypeConverter
import com.example.cinelog.domain.model.ListType

object Converters {
    @TypeConverter
    fun fromListType(value: ListType): String {
        return value.name
    }

    @TypeConverter
    fun toListType(value: String): ListType {
        return ListType.valueOf(value)
    }
}
