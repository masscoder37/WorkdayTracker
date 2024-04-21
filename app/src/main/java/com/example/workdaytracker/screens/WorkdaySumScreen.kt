package com.example.workdaytracker.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun WorkdaySumScreen(navController: NavController, workStartTime: Long, workEndTime: Long, workDuration : Long, pauseDuration : Long ,date: LocalDate) {
    val context = LocalContext.current

    //implement code for tracking & resuming app state
    val sharedPreferences = context.applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    /*sharedPreferences.edit(){
        putBoolean(Constants.WORK_TRACKING_ACTIVE_KEY, false)
        putLong(Constants.START_DATE, 0L)
        putLong(Constants.TRACKING_START_TIME_KEY, 0L)
        putLong(Constants.WORK_DURATION_KEY, 0L)
        putLong(Constants.PAUSE_DURATION_KEY, 0L)
        putString(Constants.WORK_MODE_ACTIVE_KEY, "working")
        apply()
    }*/


    //handle work start time and end time here
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val instantStart = remember { mutableStateOf(Instant.ofEpochMilli(workStartTime)) }
    val localTimeStart = remember { mutableStateOf(instantStart.value.atZone(ZoneId.systemDefault()).toLocalTime()) }
    val startTimeString = remember { mutableStateOf(localTimeStart.value.format(timeFormatter)) }


    val instantEnd = remember { mutableStateOf(Instant.ofEpochMilli(workEndTime)) }
    val localTimeEnd = remember { mutableStateOf(instantEnd.value.atZone(ZoneId.systemDefault()).toLocalTime()) }
    val endTimeString = remember { mutableStateOf(localTimeEnd.value.format(timeFormatter)) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Workday Summary", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(150.dp))

        //Date
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Work date:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            val formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
            val formattedDate = date.format(formatter)

            Text(formattedDate, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Work duration
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Work duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            val workHours = TimeUnit.MILLISECONDS.toHours(workDuration).toInt()
            val workMinutes = (TimeUnit.MILLISECONDS.toMinutes(workDuration) %60).toInt()
            val workSeconds = (TimeUnit.MILLISECONDS.toSeconds(workDuration) %60).toInt()

            Text(text = String.format("%02d:%02d:%02d (hh:mm:ss)", workHours, workMinutes, workSeconds), fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Pause duration
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Pause duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            val pauseHours = TimeUnit.MILLISECONDS.toHours(pauseDuration).toInt()
            val pauseMinutes = (TimeUnit.MILLISECONDS.toMinutes(pauseDuration) %60).toInt()
            val pauseSeconds = (TimeUnit.MILLISECONDS.toSeconds(pauseDuration) %60).toInt()
            Text(text = String.format("%02d:%02d:%02d (hh:mm:ss)", pauseHours, pauseMinutes, pauseSeconds), fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(30.dp))


        //Work Start Time
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Work started at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))


            Text(text = startTimeString.value, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Work End Time
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Work ended at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))


            Text(text = endTimeString.value, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))



        Button(onClick = { navController.navigate("homePage") }) {
            Text("Back")
        }
    }
}






@Preview(showBackground = true)
@Composable
fun WorkdaySumScreenPreview(){
    val navController = rememberNavController()
    WorkdaySumScreen(navController = navController , workStartTime = System.currentTimeMillis(), workEndTime = (System.currentTimeMillis() + 28800000L), workDuration = 28800000L, pauseDuration = 3600000L,date = LocalDate.now())
}