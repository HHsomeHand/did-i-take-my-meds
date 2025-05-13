package com.github.hhsomehand.ui

import android.text.format.DateUtils.formatDateTime
import android.widget.Button
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.hhsomehand.model.MedRecord
import com.github.hhsomehand.ui.dialog.TimePickerDialog
import com.github.hhsomehand.ui.dialog.getDialogBoxModifier
import com.github.hhsomehand.ui.theme.MyLightGray
import com.github.hhsomehand.ui.theme.MyWhite
import com.github.hhsomehand.ui.theme.Spacing
import com.github.hhsomehand.utils.rememberSharedState
import com.github.hhsomehand.viewmodel.HomeViewModel
import toTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun RecordSection() {
    Column {
        RecordButton()

        Spacer(Modifier.height(Spacing.SMALL.value))

        ShowRecordButton()
    }
}

@Composable
fun RecordButton() {
    val viewModel: HomeViewModel = viewModel()

    Button(
        onClick = {
            viewModel.addRecord(MedRecord(LocalDateTime.now()))
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("记录服药")
    }
}

@Composable
fun ShowRecordButton() {
    val viewModel: HomeViewModel = viewModel()
    var isShowDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        onClick = {
            isShowDialog = true
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("显示记录")
    }

    ShowRecordDialog(
        isShowDialog = isShowDialog,
        onDismissRequest = {
            isShowDialog = false
        }
    ) {
        ShowRecordDialogContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRecordDialog(
    isShowDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {

    AnimatedVisibility(isShowDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest
        ) {
            Box(
                getDialogBoxModifier()
                    .width(250.dp)
                    .height(400.dp)
                    .padding(top = 10.dp)
            ) {
                content()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShowRecordDialogContent() {
    val viewModel: HomeViewModel = viewModel()

    var isShowTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    var updateId by rememberSaveable { mutableStateOf("") }

    var initHour by rememberSaveable { mutableStateOf(0) } // 存储选择的 hour
    var initMinute by rememberSaveable { mutableStateOf(0) } // 存储选择的 minute
    var setInitFlag by rememberSaveable { mutableStateOf(false) }

    // 动态创建 TimePickerState，基于 selectedHour 和 selectedMinute
    val timePickerState = remember(setInitFlag) {
        TimePickerState(
            initialHour = initHour,
            initialMinute = initMinute,
            is24Hour = true,
        )
    }

    Column {
        Text(
            text = "过去 24 小时的服药记录:",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(Spacing.SMALL.value))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            // 默认顺序是由旧到新, 这里要反转, 方便用户查看
            items(
                items = viewModel.recordList.sortedByDescending { it.date },
                key = {it.id}
            ) { medRecord ->
                MedRecordDisplayer(
                    medRecord = medRecord,
                    modifier = Modifier
                        .clickable {
                            // 这里的 index 为反转的, 这里要转正
                            updateId = medRecord.id

                            initHour = medRecord.date.hour

                            setInitFlag = !setInitFlag

                            initMinute = medRecord.date.minute // 都报错, 我如何再次修改 initialMinute

                            isShowTimePickerDialog = true
                        }
                        .animateItem(
                            // 自定义动画规格
                            placementSpec = tween(
                                durationMillis = 1000, // 动画时长 1000ms
                                delayMillis = 200,    // 延迟 200ms
                                easing = FastOutSlowInEasing // 缓动曲线
                            )
                        )
                )
            }
        }
    }

    TimePickerDialog(
        isShowDialog = isShowTimePickerDialog,
        onConfirm = {
            isShowTimePickerDialog = false

            if (updateId != "") {
                viewModel.updateRecord(updateId, timePickerState.hour, timePickerState.minute)

                updateId = ""
            }
        },
        onDismiss = {
            isShowTimePickerDialog = false
        },
        timePickerState = timePickerState
    )
}

@Composable
fun MedRecordDisplayer(
    medRecord: MedRecord,
    modifier: Modifier = Modifier
) {
    val fmt = formatDateTime(medRecord.date)

    Text(
        textAlign = TextAlign.Center,
        text = fmt,
        modifier = modifier
    )
}

fun formatDateTime(dateTime: LocalDateTime): String {
    val today = LocalDate.now()
    val date = dateTime.toLocalDate()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timePart = dateTime.format(timeFormatter)

    return when (date) {
        today -> "今天 $timePart"
        today.minusDays(1) -> "昨天 $timePart"
        else -> date.format(DateTimeFormatter.ofPattern("MM月dd日")) + " " + timePart
    }
}
