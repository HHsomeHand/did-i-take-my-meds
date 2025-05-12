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
}