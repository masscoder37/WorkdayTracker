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

    //number of entries
    // Query to count the number of entries in the work_data table
    @Query("SELECT COUNT(*) FROM drive_data")
    suspend fun getNumberOfEntries(): Int

    // Query to get the total summed drive duration
    @Query("SELECT SUM(driveDuration) FROM drive_data")
    fun getTotalDrivesDuration(): Long

    @Query("SELECT AVG(driveDuration) FROM drive_data WHERE destination = 'Work'")
    fun getAverageDurationWorkDrives(): Long
    @Query("SELECT AVG(driveDuration) FROM drive_data WHERE destination = 'Home'")
    fun getAverageDurationHomeDrives(): Long



    // Query to get the fastest drive its date for 'Work' destination
    @Query("SELECT MIN(driveDuration) as longestDuration, date FROM drive_data WHERE destination = 'Work' GROUP BY destination")
    fun getFastestWorkDrive(): DriveDetail


    // Query to get the slowest drive its date for 'Work' destination
    @Query("SELECT MAX(driveDuration) as longestDuration, date FROM drive_data WHERE destination = 'Work' GROUP BY destination")
    fun getSlowestWorkDrive(): DriveDetail

    // Query to get the fastest drive its date for 'Home' destination
    @Query("SELECT MIN(driveDuration) as longestDuration, date FROM drive_data WHERE destination = 'Home' GROUP BY destination")
    fun getFastestHomeDrive(): DriveDetail


    // Query to get the slowest drive its date for 'Home' destination
    @Query("SELECT MAX(driveDuration) as longestDuration, date FROM drive_data WHERE destination = 'Home' GROUP BY destination")
    fun getSlowestHomeDrive(): DriveDetail


    //Query to get the weekday with the highest average work drive duration
    @Query("""
        SELECT weekday, AVG(driveDuration) as avgDuration
        FROM drive_data WHERE destination = 'Work'
        GROUP BY weekday
        ORDER BY avgDuration DESC
        LIMIT 1
    """)
    suspend fun getSlowestWeekdayWork(): DrivesWeekdayAverage

    //Query to get the weekday with the lowest average work drive duration
    @Query("""
        SELECT weekday, AVG(driveDuration) as avgDuration
        FROM drive_data WHERE destination = 'Work'
        GROUP BY weekday
        ORDER BY avgDuration ASC
        LIMIT 1
    """)
    suspend fun getFastestWeekdayWork(): DrivesWeekdayAverage

    //Query to get the weekday with the highest average home drive duration
    @Query("""
        SELECT weekday, AVG(driveDuration) as avgDuration
        FROM drive_data WHERE destination = 'Home'
        GROUP BY weekday
        ORDER BY avgDuration DESC
        LIMIT 1
    """)
    suspend fun getSlowestWeekdayHome(): DrivesWeekdayAverage

    //Query to get the weekday with the lowest average home drive duration
    @Query("""
        SELECT weekday, AVG(driveDuration) as avgDuration
        FROM drive_data WHERE destination = 'Home'
        GROUP BY weekday
        ORDER BY avgDuration ASC
        LIMIT 1
    """)
    suspend fun getFastestWeekdayHome(): DrivesWeekdayAverage

    //get the average fuel consumption for work drives
    @Query("""
        SELECT AVG(CAST(fuelUse AS REAL)) as avgFuelUse
        FROM drive_data
        WHERE destination = 'Work'
    """)
    fun getAverageFuelConsumptionForWork(): Double

    //get the average fuel consumption for work drives
    @Query("""
        SELECT AVG(CAST(fuelUse AS REAL)) as avgFuelUse
        FROM drive_data
        WHERE destination = 'Home'
    """)
    fun getAverageFuelConsumptionForHome(): Double


}

data class DrivesWeekdayAverage(
    val weekday: String,
    val avgDuration: Double
)

data class DriveDetail(
    val longestDuration: Long,
    val date: LocalDate
)
