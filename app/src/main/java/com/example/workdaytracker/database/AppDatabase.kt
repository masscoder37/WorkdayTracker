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
import java.time.LocalTime

@Database(entities = [DriveData::class, WorkData::class], version = 3, exportSchema = false)
@TypeConverters(AppDatabase.LocalDateConverter::class, AppDatabase.LocalTimeConverter::class)
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
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

        // Migration from version 2 to version 3: the start and end times are now stored as LocalTimes instead of Longs
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //work_data
                // Step 1: Create a new table with the desired format
                database.execSQL("""
            CREATE TABLE new_work_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                workStartTime TEXT NOT NULL,
                workEndTime TEXT NOT NULL,
                workDuration INTEGER NOT NULL,
                pauseDuration INTEGER NOT NULL,
                weekday TEXT NOT NULL,
                isManuallyEdited INTEGER NOT NULL DEFAULT 0
            )
        """)
                // Step 2: Copy data from the old table to the new table, converting milliseconds to LocalTime
                database.execSQL("""
            INSERT INTO new_work_data (id, date, workStartTime, workEndTime, workDuration, pauseDuration, weekday, isManuallyEdited)
            SELECT id, date,
                   time(workStartTime / 1000, 'unixepoch', 'localtime'),
                   time(workEndTime / 1000, 'unixepoch', 'localtime'),
                   workDuration, pauseDuration, weekday, isManuallyEdited
            FROM work_data
        """)

                // Step 3: Remove the old table
                database.execSQL("DROP TABLE work_data")

                // Step 4: Rename the new table to the original table name
                database.execSQL("ALTER TABLE new_work_data RENAME TO work_data")

                //drive_data
                // Step 1: Create a new table with the desired format
                database.execSQL("""
            CREATE TABLE new_drive_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                destination TEXT NOT NULL,
                driveStartTime TEXT NOT NULL,
                driveEndTime TEXT NOT NULL,
                driveDuration INTEGER NOT NULL,
                fuelUse TEXT NOT NULL,
                comment TEXT NOT NULL,
                weekday TEXT NOT NULL,
                isManuallyEdited INTEGER NOT NULL DEFAULT 0
            )
        """)
                // Step 2: Copy data from the old table to the new table, converting milliseconds to LocalTime
                database.execSQL("""
            INSERT INTO new_drive_data (id, date, destination, driveStartTime, driveEndTime, driveDuration, fuelUse, comment, weekday, isManuallyEdited)
            SELECT id, date, destination,
                   time(driveStartTime / 1000, 'unixepoch', 'localtime'),
                   time(driveEndTime / 1000, 'unixepoch', 'localtime'),
                   driveDuration, fuelUse, comment, weekday, isManuallyEdited
            FROM drive_data
        """)

                // Step 3: Remove the old table
                database.execSQL("DROP TABLE drive_data")

                // Step 4: Rename the new table to the original table name
                database.execSQL("ALTER TABLE new_drive_data RENAME TO drive_data")

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

    object LocalTimeConverter{
        @TypeConverter
        @JvmStatic
        fun fromLocalTime(time: LocalTime?): String?{
            return time?.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toLocalTime(timeString: String?): LocalTime?{
            return timeString?.let{
                LocalTime.parse(it)
            }
        }
    }
}