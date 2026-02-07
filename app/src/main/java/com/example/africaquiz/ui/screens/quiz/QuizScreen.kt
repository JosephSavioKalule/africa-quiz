package com.example.africaquiz.ui.screens.quiz

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.africaquiz.ui.components.AnswerButton
import com.example.africaquiz.ui.components.AnswerState
import com.example.africaquiz.ui.components.CountdownTimer
import com.example.africaquiz.ui.components.GlobeWebView
import com.example.africaquiz.ui.theme.DarkNavy
import com.example.africaquiz.ui.theme.Gold
import com.example.africaquiz.ui.theme.TextGray

@Composable
fun QuizScreen(
    timerEnabled: Boolean,
    onQuizFinished: (correctCount: Int, totalQuestions: Int) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            QuizViewModel.lastResult = viewModel.getResult()
            onQuizFinished(state.score, state.totalQuestions)
        }
    }

    val question = state.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Top bar: question counter + score
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.questionNumber}/${state.totalQuestions}",
                style = MaterialTheme.typography.titleMedium,
                color = TextGray
            )
            Text(
                text = "Score: ${state.score}",
                style = MaterialTheme.typography.titleMedium,
                color = Gold
            )
        }

        // Timer
        if (timerEnabled) {
            CountdownTimer(
                isRunning = !state.isAnswerLocked,
                resetTrigger = state.currentQuestionIndex,
                onTimeUp = { viewModel.onTimeUp() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Globe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            GlobeWebView(
                modifier = Modifier.fillMaxSize(),
                capitalLat = if (state.showCapital) question.capitalLat else null,
                capitalLng = if (state.showCapital) question.capitalLng else null
            )
        }

        // Question and answers
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "What is the capital of",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = question.countryName + "?",
                style = MaterialTheme.typography.headlineMedium,
                color = Gold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            question.choices.forEach { choice ->
                val answerState = when {
                    !state.isAnswerLocked -> AnswerState.IDLE
                    choice == question.correctAnswer -> AnswerState.CORRECT
                    choice == state.selectedAnswer -> AnswerState.WRONG
                    else -> AnswerState.IDLE
                }

                AnswerButton(
                    text = choice,
                    state = answerState,
                    enabled = !state.isAnswerLocked,
                    onClick = { viewModel.selectAnswer(choice) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
