package com.example.africaquiz.ui.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.africaquiz.ui.theme.AfricanGreen
import com.example.africaquiz.ui.theme.DarkNavy
import com.example.africaquiz.ui.theme.Gold
import com.example.africaquiz.ui.theme.TextGray
import com.example.africaquiz.ui.theme.TextWhite

@Composable
fun HomeScreen(onStartQuiz: (timerEnabled: Boolean) -> Unit) {
    var timerEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Globe icon placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AfricanGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83C\uDF0D",
                fontSize = 64.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Africa Quiz",
            style = MaterialTheme.typography.headlineLarge,
            color = Gold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Test your knowledge of\nAfrican capital cities",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Timer toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Countdown Timer",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                Text(
                    text = "15 seconds per question",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
            Switch(
                checked = timerEnabled,
                onCheckedChange = { timerEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Gold,
                    checkedTrackColor = Gold.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextGray,
                    uncheckedTrackColor = TextGray.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStartQuiz(timerEnabled) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = DarkNavy
            )
        ) {
            Text(
                text = "Start Quiz",
                style = MaterialTheme.typography.labelLarge,
                color = DarkNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "10 questions • 54 African countries",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )
    }
}
