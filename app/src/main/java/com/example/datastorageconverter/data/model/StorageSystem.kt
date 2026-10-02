package com.example.datastorageconverter.data.model

enum class StorageSystem(
    val title: String,
    val base: Long
) {
    DECIMAL(
        title = "Decimal (SI)",
        base = 1000L
    ),
    BINARY(
        title = "Binary (IEC)",
        base = 1024L
    )
}
