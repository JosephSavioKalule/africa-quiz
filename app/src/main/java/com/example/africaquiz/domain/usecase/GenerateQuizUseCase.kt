package com.example.africaquiz.domain.usecase

import com.example.africaquiz.data.model.Country
import com.example.africaquiz.data.repository.QuizRepository
import com.example.africaquiz.domain.model.Question
import com.example.africaquiz.util.Constants

class GenerateQuizUseCase(private val repository: QuizRepository) {

    operator fun invoke(): List<Question> {
        val allCountries = repository.countries
        val distractors = repository.distractors
        val selected = allCountries.shuffled().take(Constants.QUESTIONS_PER_ROUND)

        return selected.map { country ->
            buildQuestion(country, allCountries, distractors)
        }
    }

    private fun buildQuestion(
        country: Country,
        allCountries: List<Country>,
        distractors: List<String>
    ): Question {
        val correctAnswer = country.capital

        // Pick 2 other capitals as wrong answers
        val otherCapitals = allCountries
            .filter { it.capital != correctAnswer }
            .shuffled()
            .take(2)
            .map { it.capital }

        // Pick 1 non-capital city as distractor
        val nonCapital = distractors
            .filter { it != correctAnswer }
            .shuffled()
            .first()

        val choices = (otherCapitals + nonCapital + correctAnswer).shuffled()

        return Question(
            countryName = country.name,
            correctAnswer = correctAnswer,
            choices = choices,
            capitalLat = country.lat,
            capitalLng = country.lng
        )
    }
}
