package com.example.workdaytracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "drive_data")
data class DriveData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val destination: String,
    //TODO: the drive start and end times are stored in milliseconds. This is not great. Better to store as a local time
    val driveStartTime: Long, //in milliseconds (Epoch)
    val driveEndTime: Long,//in milliseconds (Epoch)
    val driveDuration: Long,//in milliseconds (Epoch)
    val fuelUse: String,
    val comment: String,
    val weekday: String,
    val isManuallyEdited: Boolean = false
)