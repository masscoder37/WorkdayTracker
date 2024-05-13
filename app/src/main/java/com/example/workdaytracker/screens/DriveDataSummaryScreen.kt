package com.example.workdaytracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workdaytracker.database.AppDatabase
import com.example.workdaytracker.database.DriveDetail
import com.example.workdaytracker.database.DrivesWeekdayAverage
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

@Composable
fun DriveDataSummaryScreen(navController: NavController) {

    //set up of common variables
    val context = LocalContext.current
    //if all values are read in, 14 elements in list isLoading
    val loadingList = remember { mutableListOf<Int>() }

    val coScope = rememberCoroutineScope()

    val numberOfEntries by produceState(initialValue = 0) {
        value = AppDatabase.getDatabase(context).driveDataDao().getNumberOfEntries()
        loadingList.add(1)
    }



    val accumTotalDriveTime by produceState(initialValue = 0L) {
        value = AppDatabase.getDatabase(context).driveDataDao().getTotalDrivesDuration()
        loadingList.add(1)
    }

    val avgWorkDriveTime by produceState(initialValue = 0L) {
        value = AppDatabase.getDatabase(context).driveDataDao().getAverageDurationWorkDrives()
        loadingList.add(1)
    }
    val avgWorkFuelCons by produceState(initialValue = 0.0) {
        value = AppDatabase.getDatabase(context).driveDataDao().getAverageFuelConsumptionForWork()
        loadingList.add(1)
    }
    val slowestWorkWeekday by produceState(initialValue = DrivesWeekdayAverage("na", 0.0)) {
        value = AppDatabase.getDatabase(context).driveDataDao().getSlowestWeekdayWork()
        loadingList.add(1)
    }

    val fastestWorkWeekday by produceState(initialValue = DrivesWeekdayAverage("na", 0.0)) {
        value = AppDatabase.getDatabase(context).driveDataDao().getFastestWeekdayWork()
        loadingList.add(1)
    }

    val fastestWorkDrive by produceState(initialValue = DriveDetail(0L, LocalDate.now())) {
        value = AppDatabase.getDatabase(context).driveDataDao().getFastestWorkDrive()
        loadingList.add(1)
    }

    val slowestWorkDrive by produceState(initialValue = DriveDetail(0L, LocalDate.now())) {
        value = AppDatabase.getDatabase(context).driveDataDao().getSlowestWorkDrive()
        loadingList.add(1)
    }


    val avgHomeDriveTime by produceState(initialValue = 0L) {
        value = AppDatabase.getDatabase(context).driveDataDao().getAverageDurationHomeDrives()
        loadingList.add(1)
    }
    val avgHomeFuelCons by produceState(initialValue = 0.0) {
        value = AppDatabase.getDatabase(context).driveDataDao().getAverageFuelConsumptionForHome()
        loadingList.add(1)
    }
    val slowestHomeWeekday by produceState(initialValue = DrivesWeekdayAverage("na", 0.0)) {
        value = AppDatabase.getDatabase(context).driveDataDao().getSlowestWeekdayHome()
        loadingList.add(1)
    }

    val fastestHomeWeekday by produceState(initialValue = DrivesWeekdayAverage("na", 0.0)) {
        value = AppDatabase.getDatabase(context).driveDataDao().getFastestWeekdayHome()
        loadingList.add(1)
    }

    val fastestHomeDrive by produceState(initialValue = DriveDetail(0L, LocalDate.now())) {
        value = AppDatabase.getDatabase(context).driveDataDao().getFastestHomeDrive()
        loadingList.add(1)
    }

    val slowestHomeDrive by produceState(initialValue = DriveDetail(0L, LocalDate.now())) {
        value = AppDatabase.getDatabase(context).driveDataDao().getSlowestHomeDrive()
        loadingList.add(1)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Text(
                text = "Drive Data Summary",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (loadingList.count() != 14) {
                CircularProgressIndicator()

            } else {


                //convert to strings
                val accumTotalDriveTimeString = durationToStringHHMIN(accumTotalDriveTime)
                val avgWorkDriveTimeString = durationToStringMINSS(avgWorkDriveTime)
                val avgHomeDriveTimeString = durationToStringMINSS(avgHomeDriveTime)
                val avgWorkFuelString = String.format("%.1f L", avgWorkFuelCons)
                val avgHomeFuelString = String.format("%.1f L", avgHomeFuelCons)
                val workWeekdaySlowestDurString =
                    durationToStringMINSS(slowestWorkWeekday.avgDuration.roundToLong())
                val workWeekdayFastestDurString =
                    durationToStringMINSS(fastestWorkWeekday.avgDuration.roundToLong())
                val homeWeekdaySlowestDurString =
                    durationToStringMINSS(slowestHomeWeekday.avgDuration.roundToLong())
                val homeWeekdayFastestDurString =
                    durationToStringMINSS(fastestWorkWeekday.avgDuration.roundToLong())
                val workFastestDriveTime = durationToStringMINSS(fastestWorkDrive.extremeDuration)
                val workSlowestDriveTime = durationToStringMINSS(slowestWorkDrive.extremeDuration)
                val homeFastestDriveTime = durationToStringMINSS(fastestHomeDrive.extremeDuration)
                val homeSlowestDriveTime = durationToStringMINSS(slowestHomeDrive.extremeDuration)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    //overall # drives
                    item {
                        Text(
                            "Number of overall drives",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            numberOfEntries.toString(),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }

                    //accumulated total drive time
                    item {
                        Text(
                            "Accumulated total drive time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$accumTotalDriveTimeString (h:min)",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //work drives
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Work drives",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //average duration
                    item {
                        Text(
                            "Average driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$avgWorkDriveTimeString (min:s)",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //average fuel consumption
                    item {
                        Text(
                            "Average fuel consumption",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            avgWorkFuelString,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //Day with shortest average duration
                    item {
                        Text(
                            "Weekday with shortest avg. driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "${fastestWorkWeekday.weekday.lowercase()} ($workWeekdayFastestDurString (min:s))",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //day with longest average duration
                    item {
                        Text(
                            "Weekday with longest avg. driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "${slowestWorkWeekday.weekday.lowercase()} ($workWeekdaySlowestDurString (min:s))",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //shortest drive
                    item {
                        Text(
                            "Fastest drive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$workFastestDriveTime (min:s) (on ${fastestWorkDrive.date})",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //longest drive
                    item {
                        Text(
                            "Slowest drive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$workSlowestDriveTime (min:s) (on ${slowestWorkDrive.date})",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }

                    //home drives

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Home drives",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //average duration
                    item {
                        Text(
                            "Average driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$avgHomeDriveTimeString (min:s)",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //average fuel consumption
                    item {
                        Text(
                            "Average fuel consumption",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            avgHomeFuelString,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //Day with shortest average duration
                    item {
                        Text(
                            "Weekday with shortest avg. driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "${fastestHomeWeekday.weekday.lowercase()} ($homeWeekdayFastestDurString (min:s))",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //day with longest average duration
                    item {
                        Text(
                            "Weekday with longest avg. driving time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "${slowestHomeWeekday.weekday.lowercase()} ($homeWeekdaySlowestDurString (min:s))",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //shortest drive
                    item {
                        Text(
                            "Fastest drive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$homeFastestDriveTime (min:s) (on ${fastestHomeDrive.date})",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }
                    //longest drive
                    item {
                        Text(
                            "Slowest drive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            "$homeSlowestDriveTime (min:s) (on ${slowestHomeDrive.date})",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider(
                            color = Color.Gray, // Color of the divider
                            thickness = 1.dp, // Thickness of the divider
                            modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                        )
                    }


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), // Ensures the Row fills the horizontal space
                    horizontalArrangement = Arrangement.Center // Centers the content horizontally
                ) {
                    Button(onClick = { navController.navigate("homePage") }) {
                        Text("Home")
                    }
                    Spacer(modifier = Modifier.width(50.dp))
                    Button(onClick = { navController.navigate("driveDataGraphScreen") }) {
                        Text("Graphs")
                    }
                }
            }

        }
    }
}


fun durationToStringHHMIN(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds).toInt()
    val minutes = (TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60).toInt()
    return String.format("%02d:%02d", hours, minutes)
}

fun durationToStringMINSS(milliseconds: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds).toInt()
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun DriveSummaryScreenPreview() {
    val navController = rememberNavController()
    DriveDataSummaryScreen(navController = navController)
}