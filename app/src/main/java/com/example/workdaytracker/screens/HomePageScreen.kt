package com.example.workdaytracker.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workdaytracker.Constants
import com.example.workdaytracker.database.AppDatabase
import com.example.workdaytracker.database.DriveData
import com.example.workdaytracker.database.WorkData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.random.Random


//HomePage Screen for navigation to the driveScreen, workdayScreen or to the Statistics screen
@Composable
fun HomePageScreen(navController: NavController) {


    //variables for the tracking of the app state
    val context = LocalContext.current



    val sharedPreferences = context.applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    val isDriveTrackingActive = remember{ mutableStateOf(sharedPreferences.getBoolean(
        Constants.DRIVE_TRACKING_ACTIVE_KEY,
        false))}
    val isWorkTrackingActive = remember{ mutableStateOf(sharedPreferences.getBoolean(
        Constants.WORK_TRACKING_ACTIVE_KEY,
        false
    ))}
    //decision is only required if at least one state is true
    val decisionRequired = remember{ mutableStateOf(isDriveTrackingActive.value || isWorkTrackingActive.value)}




    //Check if a decision is required

    LaunchedEffect(decisionRequired.value){
        if(decisionRequired.value) {

            if (isDriveTrackingActive.value) {
                decisionRequired.value = false
                val destination =
                    sharedPreferences.getString(Constants.DRIVE_DESTINATION_KEY, "Home")
                val startTime = sharedPreferences.getLong(Constants.TRACKING_START_TIME_KEY, 1L)

                navController.navigate("driveScreen/$destination/$startTime")

            }



            if (isWorkTrackingActive.value) {

                val trackingStartTime = sharedPreferences.getLong(Constants.TRACKING_START_TIME_KEY, 1L)
                val previousWorkDuration = sharedPreferences.getLong(Constants.WORK_DURATION_KEY, 1L)
                val previousPauseDuration = sharedPreferences.getLong(Constants.PAUSE_DURATION_KEY, 1L)
                val previousWorkingMode =
                    sharedPreferences.getString(Constants.WORK_MODE_ACTIVE_KEY, "working")
                decisionRequired.value = false

                navController.navigate("workdayScreen/$trackingStartTime/$previousWorkDuration/$previousPauseDuration/$previousWorkingMode")

            }
        }
    }
    
    
    
    


    //UI starts here
    val destinationString = remember { mutableStateOf("Home") }

    if(!decisionRequired.value){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Workday Tracker", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Drive Tracking", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Destination:", fontSize = 14.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Work")
            Spacer(modifier = Modifier.width(5.dp))

            //switch to set drive destination
            Switch(
                checked = destinationString.value == "Home",
                onCheckedChange = { isChecked ->
                    destinationString.value = if(isChecked) "Home" else "Work"
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = Color.Green,
                    uncheckedTrackColor = Color.Red
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text("Home")
        }

        //when drive is started, need to pass the destination and the start time to calculate the drive duration
        Button(onClick = {
            val destinationToPass : String = destinationString.value
            val driveStartTime = System.currentTimeMillis()

            navController.navigate("driveScreen/$destinationToPass/$driveStartTime") }) {
            Text("Start Drive")
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Work Tracking", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))


        val previousWorkDuration = 0L
        val previousPauseDuration = 0L
        val previousWorkingMode = "working"

        Button(onClick = { navController.navigate("workdayScreen/${System.currentTimeMillis()}/$previousWorkDuration/$previousPauseDuration/$previousWorkingMode") }) {
            Text("Start Working")
        }
        Spacer(modifier = Modifier.height(300.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = {
                navController.navigate("driveDataScreen")
            }) {
                Text("Drive Data")
            }

            Spacer(modifier = Modifier.width(50.dp))

            Button(onClick = {
                navController.navigate("workDataScreen")
            }) {
                Text("Work Data")
            }
        }
    }
}}

@Preview(showBackground = true)
@Composable
fun HomePageScreenPreview(){
    val navController = rememberNavController()
    HomePageScreen(navController = navController)

}

//test: Database testing
/*fun seedDatabase(testDb: AppDatabase, coScope: CoroutineScope) {

    // Populate WorkData
    (1..100).forEach {
        val date = LocalDate.now().minusDays(it.toLong())
        val startEpoch = LocalTime.of(Random.nextInt(6,10), Random.nextInt(3,58)).atDate(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endEpoch = LocalTime.of(Random.nextInt(16,20), Random.nextInt(3,58)).atDate(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val workData = WorkData(
            date = date,
            workStartTime = startEpoch,
            workEndTime = endEpoch,
            workDuration = endEpoch - startEpoch,
            pauseDuration = Random.nextLong(1000000, 4000000),
            weekday = date.dayOfWeek.toString(),
            isManuallyEdited = false
        )
        coScope.launch {
            withContext(Dispatchers.IO) {
                val appDatabase = testDb
                appDatabase.workDataDao().insert(workData)
            }
        }
    }

    val destination = listOf<String>("Home", "Work")

    // Populate DriveData
    (1..100).forEach {
        val date = LocalDate.now().minusDays(it.toLong())
        val startEpoch = LocalTime.of(Random.nextInt(6,10), Random.nextInt(3,58)).atDate(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endEpoch = LocalTime.of(Random.nextInt(11,12), Random.nextInt(3,58)).atDate(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val driveData = DriveData(
            date = date,
            destination = destination.random(),
            driveStartTime = startEpoch,
            driveEndTime = endEpoch,
            driveDuration = endEpoch - startEpoch,
            fuelUse = Random.nextInt(4,10).toString(),
            comment = "db testing commute",
            weekday = date.dayOfWeek.toString(),
            isManuallyEdited = false
        )
        coScope.launch {
            withContext(Dispatchers.IO) {
                val appDatabase = testDb
                appDatabase.driveDataDao().insert(driveData)
            }
        }
    }
}*/

