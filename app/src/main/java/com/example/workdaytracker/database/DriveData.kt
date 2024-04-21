package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "drive_data")
data class DriveData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val destination: String,
    val driveStartTime: Long,
    val driveEndTime: Long,
    val driveDuration: Long,
    val fuelUse: String,
    val comment: String
)