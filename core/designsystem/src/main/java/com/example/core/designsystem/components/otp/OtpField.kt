package com.example.core.designsystem.components.otp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography

@Composable
fun OtpCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val otpState = rememberTextFieldState(value)
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(value, length) {
        val sanitized = value.filter { it.isDigit() }.take(length)
        if (sanitized != otpState.text.toString()) {
            otpState.edit {
                replace(0, this.length, sanitized)
            }
        }
    }
    LaunchedEffect(otpState, length) {
        snapshotFlow { otpState.text.toString() }
            .collect { raw ->
                val digits = raw.filter { it.isDigit() }.take(length)
                if (digits != value) {
                    onValueChange(digits)
                }
            }
    }

    LaunchedEffect(isError) {
        if (isError) {
            for (i in 0 until 4) {
                shakeOffset.animateTo(
                    targetValue = -5f,
                    animationSpec = tween(durationMillis = 50),
                )
                shakeOffset.animateTo(
                    targetValue = 5f,
                    animationSpec = tween(durationMillis = 50),
                )
            }
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 50),
            )
        }
    }

    BasicTextField(
        state = otpState,
        modifier = modifier.semantics {
            contentType = ContentType.SmsOtpCode
        },
        enabled = enabled,
        inputTransformation = InputTransformation.maxLength(length),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = {
            val otpCode = otpState.text.toString()
            Row(
                modifier = Modifier.fillMaxWidth()
                    .shake(isError),
                horizontalArrangement = Arrangement.SpaceAround,

            ) {
                repeat(length) { index ->
                    Digit(
                        char = otpCode.getOrElse(index) { ' ' },
                        highlight = index == otpCode.length && !isError,
                        isError = isError,
                    )
                }
            }
        },
    )
}

@Composable
private fun Digit(
    char: Char,
    highlight: Boolean,
    isError: Boolean,
) {
    val containerColor = if (char == ' ') AppColors.SurfaceHighest else AppColors.SurfaceHigher
    val targetBorderSize = when {
        isError -> 2.dp
        highlight -> 2.dp
        else -> 1.dp
    }

    val targetBorderColor = when {
        isError -> AppColors.Error
        highlight -> AppColors.TextPrimary
        else -> AppColors.TextSecondary
    }

    val borderSize by animateDpAsState(targetValue = targetBorderSize)
    val borderColor by animateColorAsState(targetValue = targetBorderColor)

    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                width = borderSize,
                color = borderColor,
                shape = RoundedCornerShape(12.dp),
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Text(
            text = char.toString(),
            style = AppTypography.Body1Medium,
            modifier = Modifier.align(Alignment.Center),
            color = if (isError) AppColors.Error else AppColors.TextPrimary,
        )
    }
}

@Composable
fun Modifier.shake(
    trigger: Boolean,
    iterations: Int = 4,
    amplitude: Dp = 5.dp,
    durationPerMoveMillis: Int = 50,
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            repeat(iterations) {
                offsetX.animateTo(
                    targetValue = -amplitude.value,
                    animationSpec = tween(durationMillis = durationPerMoveMillis),
                )
                offsetX.animateTo(
                    targetValue = amplitude.value,
                    animationSpec = tween(durationMillis = durationPerMoveMillis),
                )
            }
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = durationPerMoveMillis),
            )
        } else {
            offsetX.snapTo(0f)
        }
    }

    this.offset(x = offsetX.value.dp)
}
