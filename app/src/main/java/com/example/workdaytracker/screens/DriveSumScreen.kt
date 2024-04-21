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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun DriveSumScreen(navController: NavController, destination: String, driveStartTime: Long, driveEndTime: Long, fuelUse: Float, comment: String, date: LocalDate) {


    //for unknown reasons, reset data here again
    val context = LocalContext.current
    val sharedPreferences = context.applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit(){
        putBoolean(Constants.DRIVE_TRACKING_ACTIVE_KEY, false)
        putLong(Constants.START_DATE, 0L)
        putLong(Constants.TRACKING_START_TIME_KEY, 0L)
        putString(Constants.DRIVE_DESTINATION_KEY, "Home")
        apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Drive Summary", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(150.dp))

        //destination
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Drive destination:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Text(destination, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))

        //Date
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Drive date:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))



            val formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
            val formattedDate = date.format(formatter)

            Text(formattedDate, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))

        //Drive duration
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Drive duration:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            val driveDuration = driveEndTime - driveStartTime
            val minutes = TimeUnit.MILLISECONDS.toMinutes(driveDuration).toInt()
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(driveDuration) %60).toInt()

            Text("$minutes:$seconds (min:sec) ", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))

        //fuel use
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Fuel Usage:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            Text("$fuelUse L/100 km ", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))

        //comment
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Text("Comment:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(20.dp))

            Text(comment, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(75.dp))



        Button(onClick = { navController.navigate("homePage") }) {
            Text("Back")
        }
    }



}

@Preview(showBackground = true)
@Composable
fun DriveSumScreenPreview(){
    val navController = rememberNavController()
    DriveSumScreen(navController=navController, destination = "Home", fuelUse = 5.0f, driveStartTime = 123456789, driveEndTime = 987654321, comment = "n.a.", date = LocalDate.now() )
}