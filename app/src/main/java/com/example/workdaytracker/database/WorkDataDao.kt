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

}