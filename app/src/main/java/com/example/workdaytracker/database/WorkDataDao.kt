package com.example.workdaytracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface WorkDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workData: WorkData)

    @Query("SELECT * FROM work_data ORDER BY date DESC")
    fun getAllWorkData(): LiveData<List<WorkData>>


    @Query("SELECT DISTINCT date FROM work_Data ORDER BY date DESC")
    suspend fun getAllWorkDataDates(): List<LocalDate>

    @Query("SELECT * FROM work_data WHERE date = :selectedDate LIMIT 1 ")
    suspend fun getWorkDataForDate(selectedDate: LocalDate): WorkData

    @Update
    suspend fun updateWorkData(workData: WorkData)

    @Query("DELETE FROM work_data WHERE id = :entityId")
    suspend fun deleteById(entityId: Int)

    // Query to count the number of entries in the work_data table
    @Query("SELECT COUNT(*) FROM work_data")
    suspend fun getNumberOfEntries(): Int

    // Query to get the average working duration
    @Query("SELECT AVG(workDuration) FROM work_data")
    suspend fun getAverageWorkDuration(): Double

    // Query to find the entire WorkData object with the lowest work duration
    @Query("SELECT * FROM work_data ORDER BY workDuration ASC LIMIT 1")
    suspend fun getWorkDataWithLowestDuration(): WorkData

    // Query to find the entire WorkData object with the highest work duration
    @Query("SELECT * FROM work_data ORDER BY workDuration DESC LIMIT 1")
    suspend fun getWorkDataWithHighestDuration(): WorkData

    // Query to find the weekday with the lowest average work duration and the average duration
    @Query("""
        SELECT weekday, AVG(workDuration) as avgDuration
        FROM work_data
        GROUP BY weekday
        ORDER BY avgDuration ASC
        LIMIT 1
    """)
    suspend fun getWeekdayWithLowestAverageDuration(): WeekdayAverage

    // Query to find the weekday with the highest average work duration and the average duration
    @Query("""
        SELECT weekday, AVG(workDuration) as avgDuration
        FROM work_data
        GROUP BY weekday
        ORDER BY avgDuration DESC
        LIMIT 1
    """)
    suspend fun getWeekdayWithHighestAverageDuration(): WeekdayAverage

    // Query to find the entire WorkData object with the earliest starting time
    @Query("SELECT * FROM work_data ORDER BY workStartTime ASC LIMIT 1")
    suspend fun getWorkDataWithEarliestStart(): WorkData

    // Query to find the entire WorkData object with the latest starting time
    @Query("SELECT * FROM work_data ORDER BY workStartTime DESC LIMIT 1")
    suspend fun getWorkDataWithLatestStart(): WorkData

    // Query to find the entire WorkData object with the earliest starting time
    @Query("SELECT * FROM work_data ORDER BY workEndTime ASC LIMIT 1")
    suspend fun getWorkDataWithEarliestEnd(): WorkData

    // Query to find the entire WorkData object with the latest starting time
    @Query("SELECT * FROM work_data ORDER BY workEndTime DESC LIMIT 1")
    suspend fun getWorkDataWithLatestEnd(): WorkData

    //TODO: create queries to transfer data to the summary screen



}
data class WeekdayAverage(
    val weekday: String,
    val avgDuration: Long
)