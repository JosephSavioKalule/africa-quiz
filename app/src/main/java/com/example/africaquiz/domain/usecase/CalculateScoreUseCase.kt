package com.example.africaquiz.domain.usecase

import com.example.africaquiz.domain.model.Question
import com.example.africaquiz.domain.model.QuestionDetail
import com.example.africaquiz.domain.model.QuizResult

class CalculateScoreUseCase {

    operator fun invoke(
        questions: List<Question>,
        userAnswers: List<String?>
    ): QuizResult {
        val details = questions.mapIndexed { index, question ->
            val userAnswer = userAnswers.getOrNull(index)
            QuestionDetail(
                countryName = question.countryName,
                correctAnswer = question.correctAnswer,
                userAnswer = userAnswer,
                isCorrect = userAnswer == question.correctAnswer
            )
        }

        val correctCount = details.count { it.isCorrect }
        val total = questions.size
        val percentage = if (total > 0) (correctCount * 100) / total else 0

        return QuizResult(
            totalQuestions = total,
            correctCount = correctCount,
            scorePercentage = percentage,
            questionDetails = details
        )
    }
}
