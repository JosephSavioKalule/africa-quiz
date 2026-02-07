package com.example.africaquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.africaquiz.ui.screens.home.HomeScreen
import com.example.africaquiz.ui.screens.quiz.QuizScreen
import com.example.africaquiz.ui.screens.results.ResultsScreen

object Routes {
    const val HOME = "home"
    const val QUIZ = "quiz/{timerEnabled}"
    const val RESULTS = "results/{correctCount}/{totalQuestions}"

    fun quiz(timerEnabled: Boolean) = "quiz/$timerEnabled"
    fun results(correctCount: Int, totalQuestions: Int) = "results/$correctCount/$totalQuestions"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onStartQuiz = { timerEnabled ->
                    navController.navigate(Routes.quiz(timerEnabled))
                }
            )
        }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(
                navArgument("timerEnabled") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val timerEnabled = backStackEntry.arguments?.getBoolean("timerEnabled") ?: false
            QuizScreen(
                timerEnabled = timerEnabled,
                onQuizFinished = { correctCount, totalQuestions ->
                    navController.navigate(Routes.results(correctCount, totalQuestions)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = Routes.RESULTS,
            arguments = listOf(
                navArgument("correctCount") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val correctCount = backStackEntry.arguments?.getInt("correctCount") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
            ResultsScreen(
                correctCount = correctCount,
                totalQuestions = totalQuestions,
                onPlayAgain = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
