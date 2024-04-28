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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
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

//Show Current Duration of Drive, destination, gives possibility to end drive
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveScreen(navController: NavController, destination: String, driveStartTime:Long) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val sharedPreferences = context.applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putBoolean(Constants.DRIVE_TRACKING_ACTIVE_KEY, true)
        putLong(Constants.TRACKING_START_TIME_KEY, driveStartTime)
        putString(Constants.START_DATE, LocalDate.now().toString())
        putString(Constants.DRIVE_DESTINATION_KEY, destination)
        apply()
    }

    //handle calculating the current drive duration
    val elapsedTime = remember { mutableLongStateOf(0L) }


    //Update UI every second to display passing seconds
    LaunchedEffect(key1 = Unit){
        while(true){
            delay(1000L)
            elapsedTime.longValue= System.currentTimeMillis() - driveStartTime
        }
    }
    //convert to minutes and seconds
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime.longValue).toInt()
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(elapsedTime.longValue) %60).toInt()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Drive Screen", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(150.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){
            Text("Current destination:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Text(destination, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(100.dp))
        Text("Elapsed drive duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 24.sp)
        Spacer(modifier = Modifier.height(20.dp))



        Spacer(modifier = Modifier.height(50.dp))



        //handle the logic and variables for the dialog window
        val showDialog = remember { mutableStateOf(false) }
        val fuelUse = remember { mutableStateOf(" ") }
        val comment = remember { mutableStateOf(" ") }
        val driveEndTime = remember { mutableLongStateOf(0L)        }


        //the visible button triggers the dialog to show and records the driveEndTime
        Button(onClick = {
            driveEndTime.longValue = System.currentTimeMillis()
            showDialog.value = true }) {
            Text("End Drive")
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(text = "End Drive")
                },
                text = {
                    Column {
                        // Input for fuel use
                        OutlinedTextField(
                            value = fuelUse.value,
                            onValueChange = { fuelUse.value = it },
                            label = { Text("Fuel Use [L/100 km]") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Input for comment
                        OutlinedTextField(
                            value = comment.value,
                            onValueChange = {
                                if(it.length <= 160)
                                    comment.value = it },
                            label = { Text("Comment") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {

                        //reset tracking state
                        //doesn't seem to work here, but do it anyways
                        sharedPreferences.edit{
                            putBoolean(Constants.DRIVE_TRACKING_ACTIVE_KEY, false)
                            putLong(Constants.START_DATE, 0L)
                            putLong(Constants.TRACKING_START_TIME_KEY, 0L)
                            putString(Constants.DRIVE_DESTINATION_KEY, "Home")
                            apply()
                        }

                        showDialog.value = false
                        //apparently, if comment.value is "", it crashes, same for fuel use. No clue why
                        val cleanedComment = comment.value.trim()
                        val cleanedFuelUse = fuelUse.value.trim()

                        if (cleanedComment.isEmpty()) {
                            comment.value = "n.a."
                        }

                        if (cleanedFuelUse.isEmpty()) {
                            fuelUse.value = "0.0"
                        }

                        val systemDate = LocalDate.now()



                        //Prepare data for database
                        val driveData = DriveData(
                            date = systemDate,
                            destination = destination,
                            driveStartTime = Instant.ofEpochMilli(driveStartTime).atZone(ZoneId.systemDefault()).toLocalTime(),
                            driveEndTime = Instant.ofEpochMilli(driveEndTime.longValue).atZone(ZoneId.systemDefault()).toLocalTime() ,
                            driveDuration = (driveEndTime.longValue-driveStartTime),
                            fuelUse = fuelUse.value,
                            comment = comment.value,
                            weekday = systemDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        )

                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                val appDatabase = AppDatabase.getDatabase(context)
                                appDatabase.driveDataDao().insert(driveData = driveData)
                            }
                        }



                        navController.navigate("driveSumScreen/$destination/$driveStartTime/${driveEndTime.longValue}/${fuelUse.value}/${comment.value}/$systemDate")

                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }






    }
}

@Preview(showBackground = true)
@Composable
fun DriveScreenPreview(){
    val navController = rememberNavController()

    DriveScreen(navController, "Home", System.currentTimeMillis())
}

