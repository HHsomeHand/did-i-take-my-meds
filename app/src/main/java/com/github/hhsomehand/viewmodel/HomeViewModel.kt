package com.github.hhsomehand.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hhsomehand.model.MedRecord
import com.github.hhsomehand.utils.SharedState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel: ViewModel() {
    private val _recordList: SnapshotStateList<MedRecord> = mutableStateListOf()

    val recordList: List<MedRecord> = _recordList

    private val _recordListAdd = MutableSharedFlow<MedRecord>()

    val recordListAdd = _recordListAdd.asSharedFlow()

    fun addRecord(newRecord: MedRecord) {
        _recordList.add(newRecord)

        viewModelScope.launch {
            _recordListAdd.emit(newRecord)
        }
    }

    // 当数据更新的时候, 会传递更新的 index
    private val _recordListUpdate = MutableSharedFlow<Int>()

    val recordListUpdate = _recordListUpdate.asSharedFlow()

    fun updateRecord(
        id: String,
        hour: Int? = null,
        minute: Int? = null,
    ) {
        val index = _recordList.indexOfFirst { it.id == id }

        val date = _recordList[index].date

        val updateDateTime = date
            .withHour(hour ?: date.hour)
            .withMinute(minute ?: date.minute)

        _recordList[index] = _recordList[index].copy(
            date = updateDateTime
        )

        viewModelScope.launch {
            _recordListUpdate.emit(index)
        }
    }
}