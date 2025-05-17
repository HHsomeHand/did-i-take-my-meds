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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
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
    maxLength: Int = 4
) {
    var text by remember { mutableStateOf(value.toString()) } // 临时存储输入文本
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = text,
        onValueChange = { newText ->
            if (newText.all { it.isDigit() } && newText.length <= maxLength) {
                text = newText // 允许空值
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && text.isEmpty()) {
                    text = "0"
                    onValueChange(0)
                }
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
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

    // 当文本变化时，更新外部 value，但空值暂不处理
    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            onValueChange(text.toIntOrNull() ?: 0)
        }
    }
}