package com.github.hhsomehand.viewmodel

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hhsomehand.constant.LocalStorageConst
import com.github.hhsomehand.dao.RecordStorage
import com.github.hhsomehand.model.MedRecord
import com.github.hhsomehand.utils.LocalStorage
import com.github.hhsomehand.utils.SharedState
import com.github.hhsomehand.utils.hideAppWindow
import com.github.hhsomehand.utils.rememberSharedState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {
    private val recordStorage: RecordStorage = RecordStorage()

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

    private val _recordListInit = MutableSharedFlow<Unit>()

    val recordListInit = _recordListInit.asSharedFlow()


    init {
        viewModelScope.launch {
            val newList: List<MedRecord> = recordStorage.getRecordList()

            fun filterLast24Hours(records: List<MedRecord>): List<MedRecord> {
                val now = LocalDateTime.now()
                val twentyFourHoursAgo = now.minusHours(24)
                return records.filter { it.date.isAfter(twentyFourHoursAgo) && it.date.isBefore(now) }
            }

            val filterNewList = filterLast24Hours(newList)

            recordStorage.storeRecordList(filterNewList)

            _recordList.clear()
            _recordList.addAll(filterNewList)

            _recordListInit.emit(Unit)

            merge(
                recordListAdd,
                recordListUpdate
            ).collect {
                recordStorage.storeRecordList(recordList)
            }
        }
    }

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

    var _isHideApp = MutableStateFlow(LocalStorage.get(LocalStorageConst.isHideApp))

    val isHideApp = _isHideApp.asStateFlow()

    init {
        viewModelScope.launch {
            isHideApp.collect {
                hideAppWindow(it)
            }
        }
    }

    fun updateIsHideApp(l_isHideApp: Boolean) {
        _isHideApp.value = l_isHideApp
    }
}