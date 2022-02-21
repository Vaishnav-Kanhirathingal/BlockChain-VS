package com.kenetic.blockchainvs.datapack

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PartyData::class],
    version = 1,
    exportSchema = false
)

abstract class PartyDatabase : RoomDatabase() {
    abstract fun partyDao(): PartyDAO

    companion object {
        @Volatile
        private var INSTANCE: PartyDatabase? = null
        fun getDatabase(context: Context): PartyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, PartyDatabase::class.java, "party_database")
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }
    }
}