package com.example.datastorageconverter.data.model

import java.util.UUID

data class HistoryRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val system: StorageSystem,
    val inputValue: String,
    val inputUnit: StorageUnit,
    val formattedInput: String,
    val primaryResultFormatted: String,
    val secondaryResults: List<Pair<String, String>> = emptyList() // Pair(symbol, formattedValue)
)
