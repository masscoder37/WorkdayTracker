package com.example.workdaytracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DriveData::class, WorkData::class], version = 2, exportSchema = false)
@TypeConverters(AppDatabase.LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driveDataDao(): DriveDataDao
    abstract fun workDataDao(): WorkDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 1 to version 2: adds 'weekday' and 'isManuallyEdited' columns
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE drive_data ADD COLUMN weekday TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE drive_data ADD COLUMN isManuallyEdited INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE work_data ADD COLUMN weekday TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE work_data ADD COLUMN isManuallyEdited INTEGER NOT NULL DEFAULT 0")
            }
        }



    }
    object LocalDateConverter {
        @TypeConverter
        @JvmStatic
        fun fromLocalDate(date: LocalDate?): String? {
            return date?.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toLocalDate(dateString: String?): LocalDate? {
            return dateString?.let {
                LocalDate.parse(it)
            }
        }
    }
}