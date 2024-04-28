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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workdaytracker.database.AppDatabase
import com.example.workdaytracker.database.DriveData
import com.example.workdaytracker.database.WeekdayAverage
import com.example.workdaytracker.database.WorkData
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@Composable
fun WorkDataSummaryScreen(navController: NavController) {
//set up of common variables
    val context = LocalContext.current
    //if all values are read in, 10 elements in list isLoading
    val loadingList = remember { mutableListOf<Int>() }

    val numberOfEntries by produceState(initialValue = 0) {
        value = AppDatabase.getDatabase(context).workDataDao().getNumberOfEntries()
        loadingList.add(1)
    }

    val averageWorkDuration by produceState(initialValue = 0.0) {
        value = AppDatabase.getDatabase(context).workDataDao().getAverageWorkDuration()
        loadingList.add(2)
    }

    val minDuration by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithLowestDuration()
        loadingList.add(3)
    }

    val maxDuration by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithHighestDuration()
        loadingList.add(4)
    }

    val leastProdWeekday by produceState(initialValue = WeekdayAverage("na", 0)) {
        value = AppDatabase.getDatabase(context).workDataDao().getWeekdayWithLowestAverageDuration()
        loadingList.add(5)
    }

    val mostProdWeekday by produceState(initialValue = WeekdayAverage("na", 0)) {
        value = AppDatabase.getDatabase(context).workDataDao().getWeekdayWithHighestAverageDuration()
        loadingList.add(6)
    }

    val earliestStart by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithEarliestStart()
        loadingList.add(7)
    }

    val latestStart by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithLatestStart()
        loadingList.add(8)
    }

    val earliestEnd by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithEarliestEnd()
        loadingList.add(9)
    }

    val latestEnd by produceState(initialValue = WorkData(0, date = LocalDate.parse("2024-01-01"),
        LocalTime.parse("08:00"), LocalTime.parse("09:00"),0,0,"Monday",false )) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataWithLatestEnd()
        loadingList.add(10)
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Text(
                text = "Work Data Summary",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(50.dp))

            if (loadingList.count() != 10) {
                CircularProgressIndicator()

            } else {
                //convert to strings
                val avgWorkDurString = durationToString(Math.round(averageWorkDuration))
                val minWorkDurString = durationToString(minDuration.workDuration)
                val maxWorkDurString = durationToString(maxDuration.workDuration)
                val mostProdDayDurString = durationToString(mostProdWeekday.avgDuration)
                val leastProdDayDurString = durationToString(leastProdWeekday.avgDuration)

                //# work days tracked
                Text(
                    "Number of workdays tracked",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = numberOfEntries.toString(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))


                //Average Working duration
                Text(
                    "Average working duration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = avgWorkDurString,
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))
                //Min. Working duration
                Text(
                    "Minimum working duration (hh:mm)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$minWorkDurString (on ${minDuration.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )

                Spacer(modifier = Modifier.height(10.dp))

                //Max. Working duration
                Text(
                    "Maximum working duration (hh:mm)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$maxWorkDurString (on ${maxDuration.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))
                //Day with most working hours
                Text(
                    "Most productive weekday",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${mostProdWeekday.weekday} (avg. $mostProdDayDurString)",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Day with least working hours
                Text(
                    "Least productive weekday",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${leastProdWeekday.weekday} (avg. $leastProdDayDurString)",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Earliest clock in
                Text(
                    "Earliest clock in time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${earliestStart.workStartTime} (on ${earliestStart.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Earliest clock in
                Text(
                    "Latest clock in time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${latestStart.workStartTime} (on ${latestStart.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Earliest clock out
                Text(
                    "Earliest clock out time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${earliestEnd.workEndTime} (on ${earliestEnd.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Earliest clock out
                Text(
                    "Latest clock out time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${latestEnd.workEndTime} (on ${latestEnd.date})",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = Color.Gray, // Color of the divider
                    thickness = 1.dp, // Thickness of the divider
                    modifier = Modifier.padding(horizontal = 0.dp) // Padding to the sides of the divider
                )
                Spacer(modifier = Modifier.height(10.dp))

                //Home Button
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(), // Ensures the Row fills the horizontal space
                    horizontalArrangement = Arrangement.Center // Centers the content horizontally
                ) {
                    Button(onClick = { navController.navigate("homePage") }) {
                        Text("Home")
                    }
                    Spacer(modifier = Modifier.width(50.dp))
                    Button(onClick = { navController.navigate("workDataGraphScreen") }) {
                        Text("Graphs")
                    }
                }
            }

        }
}

fun durationToString (milliseconds: Long) : String{
    val hours = TimeUnit.MILLISECONDS.toMinutes(milliseconds).toInt()
    val minutes = (TimeUnit.MILLISECONDS.toMinutes(milliseconds) %60).toInt()
    return String.format("%02d:%02d", hours, minutes)
}

@Preview(showBackground = true)
@Composable
fun WorkSummaryScreenPreview() {
    val navController = rememberNavController()
    WorkDataSummaryScreen(navController = navController)
}