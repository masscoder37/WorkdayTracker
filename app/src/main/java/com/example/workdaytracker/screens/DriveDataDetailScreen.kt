package com.example.workdaytracker.screens

import android.app.AlertDialog
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
import com.example.workdaytracker.database.AppDatabase
import com.example.workdaytracker.database.DriveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveDataDetailScreen(navController: NavController, selectedDate: LocalDate) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }
    val isEditingWork = remember { mutableStateOf(false) }
    val isEditingHome = remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedDate = selectedDate.format(dateFormatter)


    val driveDataForDate by produceState(initialValue = listOf<DriveData>()) {
        value = AppDatabase.getDatabase(context).driveDataDao().getDriveDataForDate(selectedDate)

        //this is required due to the asynchronous fetch of the DriveData from the database
        //if it is not handled, the UI shows wrong data because initially, the DriveData element is null
        isLoading.value = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading.value) {
            // Loading UI
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {

            //driveDataForDate can contain 1 or 2 entries: depending on destination
            //first, check which destinations are present
            //add the index of the destination to the list. If list is empty, then destination does not occur
            val homePresentAt = mutableListOf<Int>()
            val workPresentAt = mutableListOf<Int>()
            for (i in driveDataForDate.indices) {
                if (driveDataForDate[i].destination == "Home")
                    homePresentAt.add(i)
                if (driveDataForDate[i].destination == "Work")
                    workPresentAt.add(i)
            }


            //column definition and main header
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Drive details for $formattedDate",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(30.dp))

                if (driveDataForDate.isEmpty())
                    Text(text = "No data available", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                else {
                    //if there is a destination present, list it here
                    //destination: Work
                    //if the home data is edited, don't show work drive data at all
                    if (!isEditingHome.value) {
                        if (workPresentAt.isNotEmpty()) {
                            //get all the necessary data here
                            val workIndex = workPresentAt[0]


                            //drive duration
                            val driveWorkDuration =
                                remember { mutableLongStateOf(driveDataForDate[workIndex].driveDuration) }

                            val workDriveDurationMinutes = remember {
                                mutableIntStateOf(
                                    TimeUnit.MILLISECONDS.toMinutes(driveWorkDuration.longValue)
                                        .toInt()
                                )
                            }

                            val workDriveDurationSeconds = remember {
                                mutableIntStateOf(
                                    (TimeUnit.MILLISECONDS.toSeconds(driveWorkDuration.longValue) % 60).toInt()
                                )
                            }

                            val workDriveDurationString = remember {
                                mutableStateOf(
                                    String.format(
                                        "%02d:%02d",
                                        workDriveDurationMinutes.intValue,
                                        workDriveDurationSeconds.intValue
                                    )
                                )
                            }

                            //fuel use
                            val driveWorkFuelUse =
                                remember { mutableStateOf(driveDataForDate[workIndex].fuelUse) }

                            //drive start time
                            val driveWorkStartMilli =
                                remember { mutableLongStateOf(driveDataForDate[workIndex].driveStartTime) }

                            val instantDriveWorkStart =
                                remember { mutableStateOf(Instant.ofEpochMilli(driveWorkStartMilli.longValue)) }

                            val localTimeWorkDriveStart = remember {
                                mutableStateOf(
                                    instantDriveWorkStart.value.atZone(ZoneId.systemDefault())
                                        .toLocalTime()
                                )
                            }

                            val driveWorkStartString = remember {
                                mutableStateOf(
                                    localTimeWorkDriveStart.value.format(timeFormatter)
                                )
                            }

                            //drive end time
                            val driveWorkEndMilli =
                                remember { mutableLongStateOf(driveDataForDate[workIndex].driveEndTime) }

                            val instantDriveWorkEnd =
                                remember { mutableStateOf(Instant.ofEpochMilli(driveWorkEndMilli.longValue)) }

                            val localTimeWorkDriveEnd = remember {
                                mutableStateOf(
                                    instantDriveWorkEnd.value.atZone(ZoneId.systemDefault())
                                        .toLocalTime()
                                )
                            }

                            val driveWorkEndString = remember {
                                mutableStateOf(
                                    localTimeWorkDriveEnd.value.format(timeFormatter)
                                )
                            }

                            //comment
                            val driveWorkComment =
                                remember { mutableStateOf(driveDataForDate[workIndex].comment) }

                            //continue displaying UI
                            Text(
                                text = "Drive to Work",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(15.dp))


                            //if editing is enabled
                            if (isEditingWork.value) {

                                //copy initial data prior to editing
                                val copyOfWorkDrive =
                                    remember { mutableStateOf(driveDataForDate[workIndex].copy()) }

                                //Drive duration
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive Duration (min:sec):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    TextField(
                                        value = workDriveDurationString.value,
                                        onValueChange = {
                                            workDriveDurationString.value = it
                                            driveWorkDuration.longValue =
                                                durationStringMinutesToMilliseconds(
                                                    it,
                                                    copyOfWorkDrive.value.driveDuration
                                                )
                                        },
                                        label = { Text("Drive Duration (min:sec)") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Fuel use
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Fuel use [L/100 km]:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    TextField(
                                        value = driveWorkFuelUse.value,
                                        onValueChange = {
                                            driveWorkFuelUse.value = it
                                        },
                                        label = { Text("Fuel use [L/100 km]") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Drive start time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive start time",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    TextField(
                                        value = driveWorkStartString.value,
                                        onValueChange = {
                                            driveWorkStartString.value = it
                                            driveWorkStartMilli.longValue =
                                                timeStringToEpochMilli(
                                                    it,
                                                    selectedDate,
                                                    copyOfWorkDrive.value.driveStartTime
                                                )
                                        },
                                        label = { Text("Drive Start Time") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Drive end time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive end time",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    TextField(
                                        value = driveWorkEndString.value,
                                        onValueChange = {
                                            driveWorkEndString.value = it
                                            driveWorkEndMilli.longValue =
                                                timeStringToEpochMilli(
                                                    it,
                                                    selectedDate,
                                                    copyOfWorkDrive.value.driveEndTime
                                                )
                                        },
                                        label = { Text("Drive End Time") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Comment
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Comment:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    TextField(
                                        value = driveWorkComment.value,
                                        onValueChange = {
                                            if (it.length <= 160)
                                                driveWorkComment.value = it
                                        },
                                        label = { Text("Comment") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))


                                //save button
                                Button(onClick = {
                                    // Create an updated driveData object from the edited fields
                                    val updatedDriveDataWork = DriveData(
                                        id = copyOfWorkDrive.value.id,
                                        date = selectedDate,
                                        driveDuration = driveWorkDuration.longValue,
                                        driveStartTime = driveWorkStartMilli.longValue,
                                        driveEndTime = driveWorkEndMilli.longValue,
                                        fuelUse = driveWorkFuelUse.value,
                                        comment = driveWorkComment.value,
                                        destination = "Work"
                                    )

                                    // Update database
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            val appDatabase = AppDatabase.getDatabase(context)
                                            appDatabase.driveDataDao()
                                                .updateDriveData(updatedDriveDataWork)
                                        }
                                    }

                                    // Set isEditing to false to exit editing mode
                                    isEditingWork.value = false
                                }) {
                                    Text("Save")
                                }

                            }

                            //standard view without editing
                            else {

                                //Drive duration
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive Duration (min:sec):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = workDriveDurationString.value,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Fuel use
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Fuel use [L/100 km]:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = driveWorkFuelUse.value,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Drive start time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive start time:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = driveWorkStartString.value,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Drive end time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Drive end time:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = driveWorkEndString.value,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                //Comment
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Comment:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = driveWorkComment.value,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { isEditingHome.value = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    IconButton(onClick = {
                                        AlertDialog.Builder(context)
                                            .setTitle("Delete Drive Entry")
                                            .setMessage("Do you want to delete the current drive entry? This action can not be undone")
                                            .setPositiveButton("Delete entry") { _, _ ->
                                                coroutineScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        val appDatabase = AppDatabase.getDatabase(context)
                                                        appDatabase.driveDataDao().deleteById(driveDataForDate[workIndex].id)
                                                    }
                                                }
                                                navController.navigate("driveDataScreen")
                                            }
                                            .setNegativeButton("Back") { _, _ ->
                                            }
                                            .show()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }
                    }
                    //destination: Home
                    if (homePresentAt.isNotEmpty()) {
                        //get all the necessary data here
                        val homeIndex = homePresentAt[0]


                        //drive duration
                        val driveHomeDuration =
                            remember { mutableLongStateOf(driveDataForDate[homeIndex].driveDuration) }

                        val homeDriveDurationMinutes = remember {
                            mutableIntStateOf(
                                TimeUnit.MILLISECONDS.toMinutes(driveHomeDuration.longValue).toInt()
                            )
                        }

                        val homeDriveDurationSeconds = remember {
                            mutableIntStateOf(
                                (TimeUnit.MILLISECONDS.toSeconds(driveHomeDuration.longValue) % 60).toInt()
                            )
                        }

                        val homeDriveDurationString = remember {
                            mutableStateOf(
                                String.format(
                                    "%02d:%02d",
                                    homeDriveDurationMinutes.intValue,
                                    homeDriveDurationSeconds.intValue
                                )
                            )
                        }

                        //fuel use
                        val driveHomeFuelUse =
                            remember { mutableStateOf(driveDataForDate[homeIndex].fuelUse) }

                        //drive start time
                        val driveHomeStartMilli =
                            remember { mutableLongStateOf(driveDataForDate[homeIndex].driveStartTime) }

                        val instantDriveHomeStart =
                            remember { mutableStateOf(Instant.ofEpochMilli(driveHomeStartMilli.longValue)) }

                        val localTimeHomeDriveStart = remember {
                            mutableStateOf(
                                instantDriveHomeStart.value.atZone(ZoneId.systemDefault())
                                    .toLocalTime()
                            )
                        }

                        val driveHomeStartString = remember {
                            mutableStateOf(
                                localTimeHomeDriveStart.value.format(timeFormatter)
                            )
                        }

                        //drive end time
                        val driveHomeEndMilli =
                            remember { mutableLongStateOf(driveDataForDate[homeIndex].driveEndTime) }

                        val instantDriveHomeEnd =
                            remember { mutableStateOf(Instant.ofEpochMilli(driveHomeEndMilli.longValue)) }

                        val localTimeHomeDriveEnd = remember {
                            mutableStateOf(
                                instantDriveHomeEnd.value.atZone(ZoneId.systemDefault())
                                    .toLocalTime()
                            )
                        }

                        val driveHomeEndString = remember {
                            mutableStateOf(
                                localTimeHomeDriveEnd.value.format(timeFormatter)
                            )
                        }

                        //comment
                        val driveHomeComment =
                            remember { mutableStateOf(driveDataForDate[homeIndex].comment) }

                        //continue displaying UI
                        Text(
                            text = "Drive to Home",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(15.dp))


                        //if editing is enabled
                        if (isEditingHome.value) {

                            //copy initial data prior to editing
                            val copyOfHomeDrive =
                                remember { mutableStateOf(driveDataForDate[homeIndex].copy()) }

                            //Drive duration
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive Duration (min:sec):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(
                                    value = homeDriveDurationString.value,
                                    onValueChange = {
                                        homeDriveDurationString.value = it
                                        driveHomeDuration.longValue =
                                            durationStringMinutesToMilliseconds(
                                                it,
                                                copyOfHomeDrive.value.driveDuration
                                            )
                                    },
                                    label = { Text("Drive Duration (min:sec)") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Fuel use
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Fuel use [L/100 km]:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(
                                    value = driveHomeFuelUse.value,
                                    onValueChange = {
                                        driveHomeFuelUse.value = it
                                    },
                                    label = { Text("Fuel use [L/100 km]") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Drive start time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive start time",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(
                                    value = driveHomeStartString.value,
                                    onValueChange = {
                                        driveHomeStartString.value = it
                                        driveHomeStartMilli.longValue =
                                            timeStringToEpochMilli(
                                                it,
                                                selectedDate,
                                                copyOfHomeDrive.value.driveStartTime
                                            )
                                    },
                                    label = { Text("Drive Start Time") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Drive end time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive end time",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(
                                    value = driveHomeEndString.value,
                                    onValueChange = {
                                        driveHomeEndString.value = it
                                        driveHomeEndMilli.longValue =
                                            timeStringToEpochMilli(
                                                it,
                                                selectedDate,
                                                copyOfHomeDrive.value.driveEndTime
                                            )
                                    },
                                    label = { Text("Drive End Time") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Comment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Comment:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                TextField(
                                    value = driveHomeComment.value,
                                    onValueChange = {
                                        if (it.length <= 160)
                                            driveHomeComment.value = it
                                    },
                                    label = { Text("Comment") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))


                            //save button
                            Button(onClick = {
                                // Create an updated driveData object from the edited fields
                                val updatedDriveDataWork = DriveData(
                                    id = copyOfHomeDrive.value.id,
                                    date = selectedDate,
                                    driveDuration = driveHomeDuration.longValue,
                                    driveStartTime = driveHomeStartMilli.longValue,
                                    driveEndTime = driveHomeEndMilli.longValue,
                                    fuelUse = driveHomeFuelUse.value,
                                    comment = driveHomeComment.value,
                                    destination = "Home"
                                )

                                // Update database
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val appDatabase = AppDatabase.getDatabase(context)
                                        appDatabase.driveDataDao()
                                            .updateDriveData(updatedDriveDataWork)
                                    }
                                }

                                // Set isEditing to false to exit editing mode
                                isEditingHome.value = false
                            }) {
                                Text("Save")
                            }

                        }

                        //standard view without editing
                        else {

                            //Drive duration
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive Duration (min:sec):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    text = homeDriveDurationString.value,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Fuel use
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Fuel use [L/100 km]:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    text = driveHomeFuelUse.value,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Drive start time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive start time:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    text = driveHomeStartString.value,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Drive end time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Drive end time:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    text = driveHomeEndString.value,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            //Comment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Comment:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    text = driveHomeComment.value,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))


                           //edit and delete buttons home drive
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { isEditingHome.value = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                IconButton(onClick = {
                                    AlertDialog.Builder(context)
                                        .setTitle("Delete Drive Entry")
                                        .setMessage("Do you want to delete the current drive entry? This action can not be undone")
                                        .setPositiveButton("Delete entry") { _, _ ->
                                            coroutineScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    val appDatabase = AppDatabase.getDatabase(context)
                                                    appDatabase.driveDataDao().deleteById(driveDataForDate[homeIndex].id)
                                                }
                                            }
                                            navController.navigate("driveDataScreen")
                                        }
                                        .setNegativeButton("Back") { _, _ ->
                                        }
                                        .show()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }

                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }
                    //Back Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { navController.navigate("driveDataScreen") }) {
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



