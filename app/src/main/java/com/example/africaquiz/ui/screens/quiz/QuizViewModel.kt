package com.example.africaquiz.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.africaquiz.data.repository.QuizRepository
import com.example.africaquiz.domain.model.Question
import com.example.africaquiz.domain.model.QuestionDetail
import com.example.africaquiz.domain.model.QuizResult
import com.example.africaquiz.domain.usecase.CalculateScoreUseCase
import com.example.africaquiz.domain.usecase.GenerateQuizUseCase
import com.example.africaquiz.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val questions: List<Question> = emptyList(),
    val score: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerLocked: Boolean = false,
    val showCapital: Boolean = false,
    val isFinished: Boolean = false,
    val userAnswers: MutableList<String?> = mutableListOf()
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val totalQuestions: Int
        get() = questions.size

    val questionNumber: Int
        get() = currentQuestionIndex + 1
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepository(application)
    private val generateQuiz = GenerateQuizUseCase(repository)
    private val calculateScore = CalculateScoreUseCase()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        startNewQuiz()
    }

    fun startNewQuiz() {
        val questions = generateQuiz()
        _uiState.value = QuizUiState(
            questions = questions,
            userAnswers = MutableList(questions.size) { null }
        )
    }

    fun selectAnswer(answer: String) {
        val state = _uiState.value
        if (state.isAnswerLocked || state.currentQuestion == null) return

        val isCorrect = answer == state.currentQuestion!!.correctAnswer
        val newScore = if (isCorrect) state.score + 1 else state.score
        val answers = state.userAnswers.toMutableList()
        answers[state.currentQuestionIndex] = answer

        _uiState.value = state.copy(
            selectedAnswer = answer,
            isAnswerLocked = true,
            showCapital = true,
            score = newScore,
            userAnswers = answers
        )

        viewModelScope.launch {
            delay(Constants.ANSWER_DELAY_MS)
            advanceToNext()
        }
    }

    fun onTimeUp() {
        val state = _uiState.value
        if (state.isAnswerLocked) return

        _uiState.value = state.copy(
            isAnswerLocked = true,
            showCapital = true,
            selectedAnswer = null
        )

        viewModelScope.launch {
            delay(Constants.ANSWER_DELAY_MS)
            advanceToNext()
        }
    }

    private fun advanceToNext() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= state.totalQuestions) {
            _uiState.value = state.copy(isFinished = true, showCapital = false)
        } else {
            _uiState.value = state.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswer = null,
                isAnswerLocked = false,
                showCapital = false
            )
        }
    }

    fun getResult(): QuizResult {
        val state = _uiState.value
        return calculateScore(state.questions, state.userAnswers)
    }

    companion object {
        var lastResult: QuizResult? = null
    }
}
