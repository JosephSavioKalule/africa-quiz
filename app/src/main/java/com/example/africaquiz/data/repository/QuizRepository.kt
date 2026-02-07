package com.example.africaquiz.data.repository

import android.content.Context
import com.example.africaquiz.data.local.AssetJsonParser
import com.example.africaquiz.data.model.Country
import com.example.africaquiz.data.model.QuizData

class QuizRepository(context: Context) {

    private val data: QuizData by lazy {
        AssetJsonParser(context.applicationContext).parse()
    }

    val countries: List<Country>
        get() = data.countries

    val distractors: List<String>
        get() = data.distractors
}
