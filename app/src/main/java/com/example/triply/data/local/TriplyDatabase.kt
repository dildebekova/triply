package com.example.triply.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Trip::class, Place::class, ScheduleItem::class, Expense::class, Note::class, Photo::class],
    version = 1,
    exportSchema = false
)
abstract class TriplyDatabase : RoomDatabase() {
    abstract fun triplyDao(): TriplyDao

    companion object {
        @Volatile
        private var Instance: TriplyDatabase? = null

        fun getDatabase(context: Context): TriplyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TriplyDatabase::class.java, "triply_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
