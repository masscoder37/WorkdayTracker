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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun WorkDataSummaryScreen(navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Text(text = "Work Data Summary", fontSize = 36.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() )
            Spacer(modifier = Modifier.height(50.dp))


            //TODO: extract the actual data from the WorkDataDao and display the real data


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
                text = "125",
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
                    text = "08 h 15 min",
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
                    text = "06:42 (on 2024-04-16)",
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
                text = "10:42 (on 2024-04-20)",
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
                text = "Tuesday (avg. 08 h 15 min)",
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
                text = "Friday (avg. 06 h 20 min)",
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
                text = "06:15 (on 2024-04-20)",
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
                text = "10:20 (on 2024-04-20)",
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
                text = "16:15 (on 2024-04-20)",
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
                text = "20:20 (on 2024-04-20)",
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
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Ensures the Row fills the horizontal space
                horizontalArrangement = Arrangement.Center // Centers the content horizontally
            ) {
                Button(onClick = { navController.navigate("homePage") }) {
                    Text("Home")
                }
            }
        }


}

@Preview(showBackground = true)
@Composable
fun WorkSummaryScreenPreview() {
    val navController = rememberNavController()
    WorkDataSummaryScreen(navController = navController)
}