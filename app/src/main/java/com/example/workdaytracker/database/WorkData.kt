package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "work_data")
data class WorkData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val workStartTime: Long,
    val workEndTime: Long,
    val workDuration: Long,
    val pauseDuration: Long,
    val weekday: String,  // New field for the day of the week
    val isManuallyEdited: Boolean = false  // New field to indicate if the entry was manually edited
)