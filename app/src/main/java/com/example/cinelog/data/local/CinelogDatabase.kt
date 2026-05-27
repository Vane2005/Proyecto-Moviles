package com.example.cinelog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cinelog.data.local.dao.UserListDao
import com.example.cinelog.data.local.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CinelogDatabase : RoomDatabase() {

    abstract fun userListDao(): UserListDao

    companion object {
        @Volatile
        private var INSTANCE: CinelogDatabase? = null

        fun getDatabase(context: Context): CinelogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CinelogDatabase::class.java,
                    "cinelog_database"
                )
                .fallbackToDestructiveMigration() // Elimina y recrea la BD si hay cambios en el schema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
