package com.example.workdaytracker.screens

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workdaytracker.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkDataDetailScreen(navController: NavController, selectedDate: LocalDate) {


    val coroutineScope = rememberCoroutineScope()

    val isLoading = remember { mutableStateOf(true) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val context = LocalContext.current
    val workDataForDate by produceState(initialValue = null as WorkData?) {
        value = AppDatabase.getDatabase(context).workDataDao().getWorkDataForDate(selectedDate)

        //this is required due to the asynchronous fetch of the WorkData from the database
        //if it is not handled, the UI shows wrong data because initially, the WorkData element is null
        isLoading.value = false
    }


    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading.value) {
            // Loading UI
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {

            //handle variables for editing
            val isEditing = remember { mutableStateOf(false) }
            val workStart = remember { mutableLongStateOf(workDataForDate!!.workStartTime) }
            val workEnd = remember { mutableLongStateOf(workDataForDate!!.workEndTime) }
            val workDuration = remember { mutableLongStateOf(workDataForDate!!.workDuration) }
            val pauseDuration = remember { mutableLongStateOf(workDataForDate!!.pauseDuration) }


            val workHours = remember {
                mutableIntStateOf(
                    TimeUnit.MILLISECONDS.toHours(workDuration.longValue).toInt()
                )
            }
            val workMinutes =
                remember { mutableIntStateOf((TimeUnit.MILLISECONDS.toMinutes(workDuration.longValue) % 60).toInt()) }


            val pauseHours = remember {
                mutableIntStateOf(
                    TimeUnit.MILLISECONDS.toHours(pauseDuration.longValue).toInt()
                )
            }
            val pauseMinutes =
                remember { mutableIntStateOf((TimeUnit.MILLISECONDS.toMinutes(pauseDuration.longValue) % 60).toInt()) }

            //display start work time in string hh:mm
            val instantStart =
                remember { mutableStateOf(Instant.ofEpochMilli(workDataForDate!!.workStartTime)) }
            val localTimeStart = remember {
                mutableStateOf(
                    instantStart.value.atZone(ZoneId.systemDefault()).toLocalTime()
                )
            }


            val startTimeString =
                remember { mutableStateOf(localTimeStart.value.format(timeFormatter)) }

            //same for end work time
            val instantEnd =
                remember { mutableStateOf(Instant.ofEpochMilli(workDataForDate!!.workEndTime)) }
            val localTimeEnd = remember {
                mutableStateOf(
                    instantEnd.value.atZone(ZoneId.systemDefault()).toLocalTime()
                )
            }
            val endTimeString =
                remember { mutableStateOf(localTimeEnd.value.format(timeFormatter)) }

            val workDurationString = remember{ mutableStateOf(String.format(
                "%02d:%02d",
                workHours.intValue,
                workMinutes.intValue
            ))}

            val pauseDurationString = remember{ mutableStateOf(String.format(
                "%02d:%02d",
                pauseHours.intValue,
                pauseMinutes.intValue
            ))}



            //variables for time picker dialogs
            val showTimePickerDialog = remember { mutableStateOf(false) }
            val isStartTimePicker = remember { mutableStateOf(true) }



            if (showTimePickerDialog.value) {
                val time = if (isStartTimePicker.value) localTimeStart.value else localTimeEnd.value
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val selectedTime = LocalTime.of(hourOfDay, minute)
                        if (isStartTimePicker.value) localTimeStart.value = selectedTime else localTimeEnd.value =
                            selectedTime
                        //error handling
                        if (localTimeEnd.value <= localTimeStart.value) {
                            localTimeEnd.value = localTimeStart.value.plusHours(1)
                        }
                        showTimePickerDialog.value = false
                    },
                    time.hour,
                    time.minute,
                    true
                ).show()
            }



            //editing view
            if (isEditing.value) {

                //make a copy of the initial data prior to submitting
                val workDataCopy = remember{ mutableStateOf(workDataForDate!!.copy())}


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    val formattedDate = workDataForDate!!.date.format(dateFormatter)



                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Work details for $formattedDate",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    //Work Duration
                    //Work Duration is fixed from start time, end time and pause duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work Duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))

                        TextField(
                            value = workDurationString.value,
                            onValueChange = {
                                workDurationString.value = it
                                workDuration.longValue = durationStringHoursToMilliseconds(it, workDataCopy.value.workDuration)
                            },
                            label = { Text("Work Duration (hh:mm)") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //Pause Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pause Duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        TextField(
                            value = pauseDurationString.value,
                            onValueChange = {
                                pauseDurationString.value = it
                                pauseDuration.longValue = durationStringHoursToMilliseconds(it, workDataCopy.value.pauseDuration)
                            },
                            label = { Text("Pause Duration (hh:mm)") }
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    //Work Start Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work started at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        TextField(
                            value = startTimeString.value,
                            onValueChange = {
                                startTimeString.value = it
                                workStart.longValue =
                                    timeStringToEpochMilli(it, workDataForDate!!.date, workDataCopy.value.workStartTime)
                            },
                            label = { Text("Work Start Time") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //Work End Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work ended at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        TextField(
                            value = endTimeString.value,
                            onValueChange = {
                                endTimeString.value = it
                                workEnd.longValue =
                                    timeStringToEpochMilli(it, workDataForDate!!.date, workDataCopy.value.workEndTime)
                            },
                            label = { Text("Work End Time") }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))





                    Button(onClick = {
                        // Create an updated workDataForDate!! object from the edited fields
                        val updatedWorkData = WorkData(
                            workStartTime = workStart.longValue,
                            workEndTime = workEnd.longValue,
                            workDuration = workDuration.longValue,
                            pauseDuration = pauseDuration.longValue,
                            date = selectedDate,
                            id = workDataForDate!!.id
                        )

                        // Update database
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                val appDatabase = AppDatabase.getDatabase(context)
                                appDatabase.workDataDao().updateWorkData(updatedWorkData)
                            }
                        }

                        // Set isEditing to false to exit editing mode
                        isEditing.value = false
                    }) {
                        Text("Save")
                    }

                }
            }
            //without editing
            else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    val formattedDate = workDataForDate!!.date.format(dateFormatter)



                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Work details for $formattedDate",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    //Work Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work Duration (hh:mm):", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = workDurationString.value
                            ,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //Pause Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pause Duration (hh:mm):", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = pauseDurationString.value,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    //Work Start Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work started at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = startTimeString.value, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //Work Start Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Work ended at:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = endTimeString.value, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isEditing.value = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButton(onClick = {
                            AlertDialog.Builder(context)
                                .setTitle("Delete Work Entry")
                                .setMessage("Do you want to delete the current work entry? This action can not be undone")
                                .setPositiveButton("Delete entry") { _, _ ->
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            val appDatabase = AppDatabase.getDatabase(context)
                                            appDatabase.workDataDao().deleteById(workDataForDate!!.id)
                                        }
                                    }
                                    navController.navigate("workDataScreen")
                                }
                                .setNegativeButton("Back") { _, _ ->
                                }
                                .show()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))



                    //Back Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { navController.navigate("workDataScreen") }) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(onClick = { navController.navigate("homePage") }) {
                            Text("Home")
                        }
                    }

                }

            }
        }
    }
}


fun timeStringToEpochMilli(timeString: String, date: LocalDate, initialValue: Long): Long {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val returnMillisecs: Long = try {
        val localTime = LocalTime.parse(timeString, formatter)
        val localDateTime = LocalDateTime.of(date, localTime)
        localDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (e: DateTimeParseException) {
        initialValue
    }

    return returnMillisecs
}

fun durationStringHoursToMilliseconds(timeString: String, initialValue: Long): Long {

    var millisecondsResult : Long
try{
        val timeStringArray = timeString.split(":")
        val timeStringHours = timeStringArray[0]
        val timeStringMinutes = timeStringArray[1]
        millisecondsResult = (timeStringHours.toLong() * 60 * 60 * 1000 + timeStringMinutes.toLong() * 60 * 1000)}
catch (e: Exception){
    millisecondsResult = initialValue
}
return millisecondsResult
}

fun durationStringMinutesToMilliseconds(timeString: String, initialValue: Long): Long {

    var millisecondsResult : Long
    try{
        val timeStringArray = timeString.split(":")
        val timeStringMinutes= timeStringArray[0]
        val timeStringSeconds = timeStringArray[1]
        millisecondsResult = (timeStringMinutes.toLong() *  60 * 1000 + timeStringSeconds.toLong()  * 1000)}
    catch (e: Exception){
        millisecondsResult = initialValue
    }
    return millisecondsResult
}
