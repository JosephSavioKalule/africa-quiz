package com.example.africaquiz.ui.screens.results

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.africaquiz.ui.screens.quiz.QuizViewModel
import com.example.africaquiz.ui.theme.CorrectGreen
import com.example.africaquiz.ui.theme.DarkNavy
import com.example.africaquiz.ui.theme.Gold
import com.example.africaquiz.ui.theme.SurfaceDark
import com.example.africaquiz.ui.theme.TextGray
import com.example.africaquiz.ui.theme.TextWhite
import com.example.africaquiz.ui.theme.WrongRed

@Composable
fun ResultsScreen(
    correctCount: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val result = QuizViewModel.lastResult

    val message = when {
        percentage >= 90 -> "Outstanding! You're an Africa expert!"
        percentage >= 70 -> "Great job! You know your capitals!"
        percentage >= 50 -> "Not bad! Keep learning!"
        percentage >= 30 -> "Keep practicing, you'll improve!"
        else -> "Time to study those capitals!"
    }

    val scoreColor = when {
        percentage >= 70 -> CorrectGreen
        percentage >= 50 -> Gold
        else -> WrongRed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quiz Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = Gold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Score circle
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$percentage%",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
                Text(
                    text = "$correctCount/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Answer review
        if (result != null) {
            Text(
                text = "Answer Review",
                style = MaterialTheme.typography.titleMedium,
                color = Gold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            result.questionDetails.forEachIndexed { index, detail ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${index + 1}. ${detail.countryName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextWhite
                        )
                        Text(
                            text = "Correct: ${detail.correctAnswer}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CorrectGreen
                        )
                        if (!detail.isCorrect && detail.userAnswer != null) {
                            Text(
                                text = "Your answer: ${detail.userAnswer}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WrongRed
                            )
                        }
                        if (detail.userAnswer == null) {
                            Text(
                                text = "No answer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }
                    }

                    Text(
                        text = if (detail.isCorrect) "\u2713" else "\u2717",
                        fontSize = 24.sp,
                        color = if (detail.isCorrect) CorrectGreen else WrongRed,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPlayAgain,
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
                text = "Play Again",
                style = MaterialTheme.typography.labelLarge,
                color = DarkNavy
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
