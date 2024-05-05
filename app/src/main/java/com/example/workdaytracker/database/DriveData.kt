package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "drive_data")
data class DriveData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val destination: String, //can be only "Home" or "Work"
    val driveStartTime: LocalTime,
    val driveEndTime: LocalTime,
    val driveDuration: Long,//in milliseconds
    val fuelUse: String,
    val comment: String,
    val weekday: String,
    val isManuallyEdited: Boolean = false
)