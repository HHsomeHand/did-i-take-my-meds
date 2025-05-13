package com.github.hhsomehand.model

import java.time.LocalDateTime
import java.util.UUID

data class MedRecord(
    val date: LocalDateTime,
    val id: String = UUID.randomUUID().toString(),
)