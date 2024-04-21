package com.example.workdaytracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface DriveDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(driveData: DriveData)

    @Query("SELECT * FROM drive_data ORDER BY date DESC")
    fun getAllDriveData(): LiveData<List<DriveData>>

    @Query("SELECT DISTINCT date FROM drive_data ORDER BY date DESC")
    suspend fun getAllDriveDataDates(): List<LocalDate>

    @Query("SELECT * FROM drive_data WHERE date = :selectedDate")
    suspend fun getDriveDataForDate(selectedDate: LocalDate): List<DriveData>

    @Update
    suspend fun updateDriveData(driveData: DriveData)

    @Query("DELETE FROM drive_data WHERE id = :entityId")
    suspend fun deleteById(entityId: Int)
}