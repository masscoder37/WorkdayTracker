package com.example.workdaytracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.workdaytracker.screens.*
import com.example.workdaytracker.database.*
import java.time.LocalDate



class MainActivity : ComponentActivity() {

    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            WorkdayTrackerApp()
        }

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "WorkdayTracker Database"
        ).build()
}


//General entry point into the app
@Composable
fun WorkdayTrackerApp() {
    val navController = rememberNavController()

    //NavHost manages how you get to different screens
    NavHost(navController = navController, startDestination = "homePage") {
        composable("homePage") { HomePageScreen(navController = navController) }

        //for the driveScreen, additional arguments are required
        //drive Screen needs: Destination, Drive start time (button press time)
        composable("driveScreen/{destination}/{buttonPressTime}") {
            val destination = it.arguments?.getString("destination") ?: "error"
            val driveStartTimeString = it.arguments?.getString("buttonPressTime") ?: "error"
            val driveStartTime = driveStartTimeString.toLongOrNull() ?: System.currentTimeMillis()

            DriveScreen(navController = navController, destination = destination, driveStartTime = driveStartTime)
        }
        //NavController needs for drive summary screen: destination, drive start time, drive end time, fuel use, comment, date of drive
        composable("driveSumScreen/{destination}/{driveStartTime}/{driveEndTime}/{fuelUse}/{comment}/{date}") {
            val destination = it.arguments?.getString("destination") ?: "error"
            val driveStartTimeString = it.arguments?.getString("driveStartTime")
            val driveStartTime = driveStartTimeString?.toLongOrNull() ?: 0
            val driveEndTimeString = it.arguments?.getString("driveEndTime")
            val driveEndTime = driveEndTimeString?.toLongOrNull() ?: System.currentTimeMillis()
            val fuelUseString = it.arguments?.getString("fuelUse") ?: "error"
            val fuelUse = fuelUseString.toFloatOrNull() ?: 0.0
            val comment = it.arguments?.getString("comment") ?: "n.a."
            val date = LocalDate.parse(it.arguments?.getString("date")) ?: LocalDate.now()
            DriveSumScreen(navController = navController, destination = destination, driveStartTime = driveStartTime, driveEndTime = driveEndTime, fuelUse = fuelUse as Float, comment=comment, date = date) }

        composable("workdayScreen/{workStartTime}/{workDuration}/{pauseDuration}/{workingMode}") {
            val workStartTimeString = it.arguments?.getString("workStartTime")
            val workStartTime = workStartTimeString?.toLongOrNull() ?: System.currentTimeMillis()
            val workDurationString =  it.arguments?.getString("workDuration")
            val pauseDurationString = it.arguments?.getString("pauseDuration")
            val workDuration = workDurationString?.toLongOrNull()  ?: 0L
            val pauseDuration = pauseDurationString?.toLongOrNull()  ?: 0L

            val workingMode = it.arguments?.getString("workingMode") ?: "working"
            WorkdayScreen(navController = navController, trackingStartTime = workStartTime, previousWorkDuration = workDuration, previousPauseDuration = pauseDuration, previousWorkingMode = workingMode) }


        composable("workdaySumScreen/{workStartTime}/{workEndTime}/{workDuration}/{pauseDuration}/{date}") {
            val workStartTimeString = it.arguments?.getString("workStartTime")
            val workStartTime = workStartTimeString?.toLongOrNull() ?: System.currentTimeMillis()
            val workEndTimeString = it.arguments?.getString("workEndTime")
            val workEndTime = workEndTimeString?.toLongOrNull() ?: System.currentTimeMillis()
            val workDurationString = it.arguments?.getString("workDuration")
            val workDuration = workDurationString?.toLongOrNull() ?: System.currentTimeMillis()
            val pauseDurationString = it.arguments?.getString("pauseDuration")
            val pauseDuration = pauseDurationString?.toLongOrNull() ?: System.currentTimeMillis()
            val date = LocalDate.parse(it.arguments?.getString("date")) ?: LocalDate.now()
            WorkdaySumScreen(navController = navController, workStartTime = workStartTime, workEndTime = workEndTime, workDuration = workDuration, pauseDuration = pauseDuration, date=date) }

        composable("workDataScreen") { WorkDataScreen(navController = navController) }
        composable("driveDataScreen") { DriveDataScreen(navController = navController) }

        composable("driveDataDetailScreen/{date}") {
            val date = LocalDate.parse(it.arguments?.getString("date")) ?: LocalDate.now()
            DriveDataDetailScreen(navController = navController, selectedDate = date) }
        composable("workDataDetailScreen/{date}") {
            val date = LocalDate.parse(it.arguments?.getString("date")) ?: LocalDate.now()
            WorkDataDetailScreen(navController = navController, selectedDate = date) }

        composable("manualDriveAddScreen") { ManualDriveAddScreen(navController = navController) }

        composable("manualWorkAddScreen") { ManualWorkAddScreen(navController = navController) }
        composable("workDataSummaryScreen") { WorkDataSummaryScreen(navController = navController) }
        composable("driveDataSummaryScreen") { DriveDataSummaryScreen(navController = navController) }
        composable("workDataGraphScreen") { WorkDataGraphScreen(navController = navController) }
        composable("driveDataGraphScreen") { DriveDataGraphScreen(navController = navController) }

    }






}}



//original MainActivity
/*class MainActivity : ComponentActivity() {

    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorkdayTrackerApp()
        }

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "WorkdayTracker Database"
        ).build()
    }
}*/