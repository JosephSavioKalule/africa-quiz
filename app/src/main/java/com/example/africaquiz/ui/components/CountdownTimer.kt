package com.example.africaquiz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.africaquiz.ui.theme.Gold
import com.example.africaquiz.ui.theme.WrongRed
import com.example.africaquiz.util.Constants
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    isRunning: Boolean,
    resetTrigger: Int,
    onTimeUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsLeft by remember { mutableIntStateOf(Constants.TIMER_DURATION_SECONDS) }

    LaunchedEffect(resetTrigger) {
        secondsLeft = Constants.TIMER_DURATION_SECONDS
    }

    LaunchedEffect(isRunning, resetTrigger) {
        if (!isRunning) return@LaunchedEffect
        secondsLeft = Constants.TIMER_DURATION_SECONDS
        while (secondsLeft > 0) {
            delay(1000L)
            if (!isRunning) return@LaunchedEffect
            secondsLeft--
        }
        onTimeUp()
    }

    val progress by animateFloatAsState(
        targetValue = secondsLeft.toFloat() / Constants.TIMER_DURATION_SECONDS,
        animationSpec = tween(300),
        label = "timerProgress"
    )

    val timerColor = if (secondsLeft <= 5) WrongRed else Gold

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "${secondsLeft}s",
            style = MaterialTheme.typography.labelLarge,
            color = timerColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = timerColor,
            trackColor = timerColor.copy(alpha = 0.2f),
        )
    }
}
