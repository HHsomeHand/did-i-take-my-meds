package com.github.hhsomehand.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CornNumberField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minWidth: Dp = 30.dp,
    maxLength: Int = 4,
) {


    BasicTextField(
        value = value.toString(),
        onValueChange = { newText ->
            // 可选：进一步限制只能输入数字
            if (newText.all { it.isDigit() } && newText.length < maxLength) {
                onValueChange(newText?.toIntOrNull() ?: 0)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Column(
                modifier = modifier
                    .defaultMinSize(minWidth = minWidth)
                    .width(IntrinsicSize.Max)
            ) {
                innerTextField()

                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        },
        textStyle = TextStyle.Default.copy(
            textAlign = TextAlign.Center
        )
    )
}