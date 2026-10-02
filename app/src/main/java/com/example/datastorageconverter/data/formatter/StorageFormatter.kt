package com.example.datastorageconverter.data.formatter

import com.example.datastorageconverter.data.model.StorageUnit
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object StorageFormatter {
    private val US_SYMBOLS = DecimalFormatSymbols(Locale.US)

    // Scientific notation threshold limits
    private val SCIENTIFIC_UPPER_LIMIT = BigDecimal("10000000000000000") // 10^16
    private val SCIENTIFIC_LOWER_LIMIT = BigDecimal("0.000001") // 10^-6

    /**
     * Formats a BigDecimal storage value according to specification:
     * - Avoids unnecessary trailing zeros (1.5 instead of 1.5000).
     * - Uses grouping separators for large values (1,024, 1,500,000).
     * - Preserves meaningful decimal precision (up to 8 decimal places).
     * - Uses readable scientific notation only when values become extremely large or small.
     */
    fun formatValue(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val absValue = value.abs()

        if (absValue >= SCIENTIFIC_UPPER_LIMIT || (absValue > BigDecimal.ZERO && absValue < SCIENTIFIC_LOWER_LIMIT)) {
            val sciFormat = DecimalFormat("0.####E0", US_SYMBOLS)
            return sciFormat.format(value).replace("E", "e")
        }

        val standardFormat = DecimalFormat("#,##0.########", US_SYMBOLS).apply {
            roundingMode = RoundingMode.HALF_UP
        }

        return standardFormat.format(value)
    }

    /**
     * Formats value with unit symbol, e.g., "1.5 GB", "1,500 MB".
     */
    fun formatWithSymbol(value: BigDecimal, unit: StorageUnit): String {
        val formattedNumber = formatValue(value)
        return "$formattedNumber ${unit.symbol}"
    }

    /**
     * Formats value with unit label, e.g., "1.5 Gigabytes".
     */
    fun formatWithLabel(value: BigDecimal, unit: StorageUnit): String {
        val formattedNumber = formatValue(value)
        val label = if (value.compareTo(BigDecimal.ONE) == 0) unit.label else "${unit.label}s"
        return "$formattedNumber $label"
    }
}
