package com.example.workdaytracker.screens

import android.app.AlertDialog
import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workdaytracker.Constants
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import com.example.workdaytracker.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale


//shows workday duration, pause duration, gives ability to pause/resume and end work
@Composable
fun WorkdayScreen(
    navController: NavController,
    trackingStartTime: Long,
    previousWorkDuration: Long,
    previousPauseDuration: Long,
    previousWorkingMode: String
) {

    //for saving of data in database
    val coroutineScope = rememberCoroutineScope()

    //start time of initial tracking start
    val startTime = trackingStartTime


    val pauseTimer = remember { mutableLongStateOf(0L) }


    val currentWorkingState = remember { mutableStateOf(previousWorkingMode) }

    //duration of worktimer: time now - start time - pause duration
    val workTimer = remember { mutableLongStateOf(0L) }


    //for tracking of app state
    val context = LocalContext.current
    val sharedPreferences =
        context.applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    val updatedPreferences = remember { mutableStateOf(false) }

    //change preferences only once
    LaunchedEffect(!updatedPreferences.value) {
        //initialize pauseTimer
        if (previousWorkingMode == "working") {
            pauseTimer.longValue = previousPauseDuration
        } else {
            pauseTimer.longValue = System.currentTimeMillis() - startTime - previousWorkDuration
        }

        workTimer.longValue = System.currentTimeMillis() - startTime - pauseTimer.longValue

        sharedPreferences.edit {
            putBoolean(Constants.WORK_TRACKING_ACTIVE_KEY, true)
            putLong(Constants.TRACKING_START_TIME_KEY, trackingStartTime)
            putString(Constants.START_DATE, LocalDate.now().toString())
            putLong(Constants.PAUSE_DURATION_KEY, pauseTimer.longValue)
            putLong(Constants.WORK_DURATION_KEY, workTimer.longValue)
            putString(Constants.WORK_MODE_ACTIVE_KEY, currentWorkingState.value)
            commit()
        }
        updatedPreferences.value = true
    }


    //for display reasons: calculation of hours, minutes, seconds
    val workHours = remember { mutableLongStateOf(0L) }
    val workMinutes = remember { mutableLongStateOf(0L) }
    val workSeconds = remember { mutableLongStateOf(0L) }

    val pauseHours = remember { mutableLongStateOf(0L) }
    val pauseMinutes = remember { mutableLongStateOf(0L) }
    val pauseSeconds = remember { mutableLongStateOf(0L) }

    //for recomposition
    workHours.longValue = TimeUnit.MILLISECONDS.toHours(workTimer.longValue)
    workMinutes.longValue = TimeUnit.MILLISECONDS.toMinutes(workTimer.longValue) % 60
    workSeconds.longValue = TimeUnit.MILLISECONDS.toSeconds(workTimer.longValue) % 60

    pauseHours.longValue = TimeUnit.MILLISECONDS.toHours(pauseTimer.longValue)
    pauseMinutes.longValue = TimeUnit.MILLISECONDS.toMinutes(pauseTimer.longValue) % 60
    pauseSeconds.longValue = TimeUnit.MILLISECONDS.toSeconds(pauseTimer.longValue) % 60


    //Update UI every second to display passing seconds
    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1000L)
            if (currentWorkingState.value == "working") {
                workTimer.longValue = System.currentTimeMillis() - startTime - pauseTimer.longValue
                workHours.longValue = TimeUnit.MILLISECONDS.toHours(workTimer.longValue)
                workMinutes.longValue = TimeUnit.MILLISECONDS.toMinutes(workTimer.longValue) % 60
                workSeconds.longValue = TimeUnit.MILLISECONDS.toSeconds(workTimer.longValue) % 60

                sharedPreferences.edit {
                    putLong(Constants.WORK_DURATION_KEY, workTimer.longValue)
                    commit()
                }
            } else {
                pauseTimer.longValue = System.currentTimeMillis() - startTime - workTimer.longValue
                pauseHours.longValue = TimeUnit.MILLISECONDS.toHours(pauseTimer.longValue)
                pauseMinutes.longValue = TimeUnit.MILLISECONDS.toMinutes(pauseTimer.longValue) % 60
                pauseSeconds.longValue = TimeUnit.MILLISECONDS.toSeconds(pauseTimer.longValue) % 60

                //track this in the shared preferences
                sharedPreferences.edit {
                    putLong(Constants.PAUSE_DURATION_KEY, pauseTimer.longValue)
                    commit()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Workday Screen", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(130.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Currently:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Text(currentWorkingState.value, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text("Elapsed working duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = String.format(
                "%02d:%02d:%02d",
                workHours.longValue,
                workMinutes.longValue,
                workSeconds.longValue
            ), fontSize = 35.sp
        )



        Spacer(modifier = Modifier.height(30.dp))

        Text("Elapsed pause duration:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = String.format(
                "%02d:%02d:%02d",
                pauseHours.longValue,
                pauseMinutes.longValue,
                pauseSeconds.longValue
            ), fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(130.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                //On clicking here, the working value is changed and the timers are adjusted accordingly
                if (currentWorkingState.value == "working") {

                    currentWorkingState.value = "relaxing"
                    sharedPreferences.edit {
                        putString(Constants.WORK_MODE_ACTIVE_KEY, currentWorkingState.value)
                        commit()
                    }
                } else {
                    currentWorkingState.value = "working"
                    sharedPreferences.edit {
                        putString(Constants.WORK_MODE_ACTIVE_KEY, "working")
                        commit()
                    }
                }

            }) {
                if (currentWorkingState.value == "working")
                    Text("Pause")
                else
                    Text("Resume")
            }
            Spacer(modifier = Modifier.width(40.dp))

            //clicking this button navigates to the WorkdaySumScreen and saves the Workday Data in the database
            Button(onClick = {

                val systemDate = LocalDate.now()
                val workEndTime = System.currentTimeMillis()

                //Prepare data for database
                val workData = WorkData(
                    date = systemDate,
                    workStartTime = Instant.ofEpochMilli(trackingStartTime).atZone(ZoneId.systemDefault()).toLocalTime(),
                    workEndTime = Instant.ofEpochMilli(workEndTime).atZone(ZoneId.systemDefault()).toLocalTime(),
                    workDuration = workTimer.longValue,
                    pauseDuration = pauseTimer.longValue,
                    weekday = systemDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                )

                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val appDatabase = AppDatabase.getDatabase(context)
                        appDatabase.workDataDao().insert(workData)
                    }
                }

                //doesn't seem to work here, but do it anyways
                sharedPreferences.edit {
                    putBoolean(Constants.WORK_TRACKING_ACTIVE_KEY, false)
                    putLong(Constants.START_DATE, 0L)
                    putLong(Constants.TRACKING_START_TIME_KEY, 0L)
                    putLong(Constants.WORK_DURATION_KEY, 0L)
                    putLong(Constants.PAUSE_DURATION_KEY, 0L)
                    putString(Constants.WORK_MODE_ACTIVE_KEY, "working")
                    commit()
                }
                navController.navigate("workdaySumScreen/$trackingStartTime/$workEndTime/${workTimer.longValue}/${pauseTimer.longValue}/$systemDate")

            }) {
                Text("End Work")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        //Abort button
        Button(onClick = {

            AlertDialog.Builder(context)
                .setTitle("Abort work tracking?")
                .setMessage("Do you want to abort the current work tracking session? No data will be saved in that case!")
                .setPositiveButton("Abort Tracking") { _, _ ->
                    with(sharedPreferences.edit()) {
                        putBoolean(Constants.WORK_TRACKING_ACTIVE_KEY, false)
                        putLong(Constants.START_DATE, 0L)
                        putLong(Constants.TRACKING_START_TIME_KEY, 0L)
                        putLong(Constants.WORK_DURATION_KEY, 0L)
                        putLong(Constants.PAUSE_DURATION_KEY, 0L)
                        putString(Constants.WORK_MODE_ACTIVE_KEY, "working")
                        commit()
                    }
                    navController.navigate("homePage")
                }
                .setNegativeButton("Continue Tracking") { _, _ ->
                }
                .show()

        }) {
            Text("Abort Work Tracking")
        }


    }


}

@Preview(showBackground = true)
@Composable
fun WorkdayScreenPreview(){
    val navController = rememberNavController()

    WorkdayScreen(navController, (System.currentTimeMillis() - 20000),360000L,6000, "working")
}

