package com.mandy_34601465.kitahack

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavHostController) {
    var loadingProgress by remember { mutableFloatStateOf(0f) }

    // Simulate loading process with a delay
    LaunchedEffect(Unit) {
        // Simulate a loading process (you can adjust the delay for real use)
        delay(3000)  // 3 seconds delay before moving to the next screen
        navController.navigate("chat") // Navigate to the chat screen
    }

    val progress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(5000)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Oltie",
                fontWeight = Bold,
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
        }
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.oltie),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(60.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = "Always your companion.",
            fontStyle = Italic,
            color = Color.Gray,
            fontSize = 20.sp
        )

        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            trackColor = Color.Gray,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Update loading progress over time
    LaunchedEffect(Unit) {
        for (i in 0..100 step 5) {
            delay(150) // Delay for smooth loading progress
            loadingProgress = i / 100f
        }
    }
}