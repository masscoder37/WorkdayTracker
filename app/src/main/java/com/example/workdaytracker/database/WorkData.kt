package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime


@Entity(tableName = "work_data")
data class WorkData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val workStartTime: LocalTime,
    val workEndTime: LocalTime,
    val workDuration: Long,//in milliseconds
    val pauseDuration: Long,//in milliseconds
    val weekday: String,
    val isManuallyEdited: Boolean = false
)