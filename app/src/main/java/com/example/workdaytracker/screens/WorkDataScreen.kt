package com.example.workdaytracker.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workdaytracker.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkDataScreen(navController: NavController) {

    val context = LocalContext.current

    val workDataDates by produceState(initialValue = listOf<LocalDate>()) {
        value = AppDatabase.getDatabase(context).workDataDao().getAllWorkDataDates()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Work Date List", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(50.dp))

        LazyColumn {
            items(workDataDates) { workDay ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp),
                    shape = RoundedCornerShape(8.dp) // This rounds the corners of the card
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(16.dp)
                            .clickable (interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)){
                                navController.navigate("workDataDetailScreen/$workDay")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val formatter = DateTimeFormatter.ofPattern("dd. MMM yyyy")
                        val formattedDate = workDay.format(formatter)
                        Text(text = formattedDate, color = Color.Black, textAlign = TextAlign.Center, fontSize = 14.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        //buttons for manual entry of data and back

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = { navController.navigate("homePage") }) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(30.dp))

            Button(onClick = {
                navController.navigate("manualWorkAddScreen")
            }) {
                Text("Manual Entry")
            }

        }



    }
}


@Preview(showBackground = true)
@Composable
fun WorkDataScreenPreview() {
    val navController = rememberNavController()
    WorkDataScreen(navController = navController)
}