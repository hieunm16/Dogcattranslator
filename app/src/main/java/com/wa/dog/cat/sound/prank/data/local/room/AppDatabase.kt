package com.wa.dog.cat.sound.prank.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wa.dog.cat.sound.prank.data.local.room.dao.LanguageDao
import com.wa.dog.cat.sound.prank.data.model.LanguageUI
import com.wa.dog.cat.sound.prank.utils.Converters

@Database(
    entities = [LanguageUI::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun languageDao(): LanguageDao

    companion object {
        val DATABASE_NAME: String = "DATABASE_NAME"
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }

    }
}