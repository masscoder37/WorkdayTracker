package com.example.workdaytracker.database

import android.content.Context
import androidx.lifecycle.LiveData

class Repository(context: Context) {
    private val driveDataDao = AppDatabase.getDatabase(context).driveDataDao()
    private val workDataDao = AppDatabase.getDatabase(context).workDataDao()

    suspend fun insertDriveData(driveData: DriveData) {
        driveDataDao.insert(driveData)
    }

    suspend fun insertWorkData(workData: WorkData) {
        workDataDao.insert(workData)
    }

    val allDriveData: LiveData<List<DriveData>> = driveDataDao.getAllDriveData()
    val allWorkData: LiveData<List<WorkData>> = workDataDao.getAllWorkData()
}
