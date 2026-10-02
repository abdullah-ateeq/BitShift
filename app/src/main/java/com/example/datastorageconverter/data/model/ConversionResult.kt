package com.example.datastorageconverter.data.model

import java.math.BigDecimal

data class ConversionResult(
    val unit: StorageUnit,
    val rawValue: BigDecimal,
    val formattedValue: String,
    val formattedWithSymbol: String,
    val isSelectedInput: Boolean
)
