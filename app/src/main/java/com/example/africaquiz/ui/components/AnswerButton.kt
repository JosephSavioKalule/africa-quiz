package com.example.africaquiz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.africaquiz.ui.theme.CardDark
import com.example.africaquiz.ui.theme.CorrectGreen
import com.example.africaquiz.ui.theme.Gold
import com.example.africaquiz.ui.theme.TextWhite
import com.example.africaquiz.ui.theme.WrongRed

enum class AnswerState {
    IDLE,
    CORRECT,
    WRONG,
    REVEALED
}

@Composable
fun AnswerButton(
    text: String,
    state: AnswerState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = when (state) {
            AnswerState.IDLE -> CardDark
            AnswerState.CORRECT -> CorrectGreen
            AnswerState.WRONG -> WrongRed
            AnswerState.REVEALED -> CorrectGreen.copy(alpha = 0.6f)
        },
        animationSpec = tween(300),
        label = "answerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            AnswerState.IDLE -> Gold.copy(alpha = 0.3f)
            AnswerState.CORRECT -> CorrectGreen
            AnswerState.WRONG -> WrongRed
            AnswerState.REVEALED -> CorrectGreen
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = TextWhite,
            disabledContainerColor = containerColor,
            disabledContentColor = TextWhite
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
