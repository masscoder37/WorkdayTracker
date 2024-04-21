package com.example.workdaytracker.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workdaytracker.database.AppDatabase
import com.example.workdaytracker.database.WorkData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualWorkAddScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val date = remember { mutableStateOf(LocalDate.now()) }
    val startTime = remember { mutableStateOf(LocalTime.now()) }
    val endTime =
        remember { mutableStateOf(LocalTime.now().plusHours(1)) } // Default to 1 hour later
    val pauseDuration = remember{ mutableStateOf("hh:mm")}



    val showDatePickerDialog = remember { mutableStateOf(false) }
    val showTimePickerDialog = remember { mutableStateOf(false) }
    val isStartTimePicker = remember { mutableStateOf(true) }


    if (showDatePickerDialog.value) {
        DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                date.value = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                showDatePickerDialog.value = false
            },
            date.value.year,
            date.value.monthValue - 1,
            date.value.dayOfMonth
        ).show()
    }

    if (showTimePickerDialog.value) {
        val time = if (isStartTimePicker.value) startTime.value else endTime.value
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                if (isStartTimePicker.value) startTime.value = selectedTime else endTime.value =
                    selectedTime
                //error handling
                if (endTime.value <= startTime.value) {
                    endTime.value = startTime.value.plusHours(1)
                }
                showTimePickerDialog.value = false
            },
            time.hour,
            time.minute,
            true
        ).show()
    }

    // Composable functions for UI elements
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Manual add workday", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(50.dp))

        // Date Picker Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Date: ")
            Spacer(modifier = Modifier.width(8.dp))
            Text(date.value.toString())
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = { showDatePickerDialog.value = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick a date")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        // Time Picker Fields for Start and End Time
        // Time Pickers
        //Start Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Start Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            Text(startTime.value.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = {
                showTimePickerDialog.value = true
                isStartTimePicker.value = true
            }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Pick a start time")
            }
        }

        //end time picker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("End Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            Text(endTime.value.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = {
                showTimePickerDialog.value = true
                isStartTimePicker.value = false
            }) {
                Icon(Icons.Rounded.ArrowForward, contentDescription = "Pick an end time")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //pause duration
        TextField(
            value = pauseDuration.value,
            onValueChange = { pauseDuration.value = it },
            label = { Text("Pause duration (hh:mm)") }
        )


        Spacer(modifier = Modifier.height(20.dp))

        //Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Button(onClick = {
                navController.navigate("workDataScreen")
            }) {
                Text("Back")
            }

            Spacer(modifier = Modifier.width(40.dp))

            // Save Button
            Button(onClick = {
                val startTimeMilli = startTime.value.toSecondOfDay() * 1000L
                val endTimeMilli = endTime.value.toSecondOfDay() *1000L
                val pauseDurationMilli = durationStringHoursToMilliseconds(pauseDuration.value, endTimeMilli-startTimeMilli)
                var workDurationMilli = endTimeMilli - startTimeMilli - pauseDurationMilli
                if(workDurationMilli < 0)
                    workDurationMilli = 0


                val newWorkData = WorkData(
                    date = date.value,
                    workStartTime = startTimeMilli,
                    workEndTime = endTimeMilli,
                    workDuration = workDurationMilli,
                    pauseDuration = pauseDurationMilli,
                    weekday = date.value.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    isManuallyEdited = true
                )

                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val appDatabase = AppDatabase.getDatabase(context)
                        appDatabase.workDataDao().insert(newWorkData)
                    }
                    navController.navigate("workDataScreen")
                }
            }) {
                Text("Save")
            }


        }
    }
}

