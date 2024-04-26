package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "work_data")
data class WorkData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    //TODO: the work start and end times are stored in milliseconds. This is not great. Better to store as a local time
    val workStartTime: Long,//in milliseconds (Epoch)
    val workEndTime: Long,//in milliseconds (Epoch)
    val workDuration: Long,//in milliseconds
    val pauseDuration: Long,//in milliseconds
    val weekday: String,
    val isManuallyEdited: Boolean = false
)