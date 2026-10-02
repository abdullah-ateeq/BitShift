package com.example.datastorageconverter.data.converter

import com.example.datastorageconverter.data.model.StorageUnit
import java.math.BigDecimal
import java.math.MathContext

object StorageConverter {
    val BITS_PER_BYTE: BigDecimal = BigDecimal("8")
    val MATH_CONTEXT: MathContext = MathContext.DECIMAL128

    /**
     * Converts a storage value between two storage units using Bytes as the common base.
     *
     * @param value The non-negative numeric value to convert.
     * @param from The source StorageUnit.
     * @param to The target StorageUnit.
     * @return The converted value as BigDecimal with high precision.
     * @throws IllegalArgumentException if value is negative.
     */
    fun convert(
        value: BigDecimal,
        from: StorageUnit,
        to: StorageUnit
    ): BigDecimal {
        if (value < BigDecimal.ZERO) {
            throw IllegalArgumentException("Enter a value of 0 or greater.")
        }

        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }

        if (from == to) {
            return value
        }

        // 1. Convert input unit to base Bytes
        val bytes = toBytes(value, from)

        // 2. Convert base Bytes to target unit
        return fromBytes(bytes, to)
    }

    /**
     * Normalizes any storage unit value to Bytes.
     */
    fun toBytes(value: BigDecimal, from: StorageUnit): BigDecimal {
        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

        return if (from.isBit) {
            value.divide(BITS_PER_BYTE, MATH_CONTEXT)
        } else {
            value.multiply(from.bytesMultiplier, MATH_CONTEXT)
        }
    }

    /**
     * Converts Bytes to the specified target unit.
     */
    fun fromBytes(bytes: BigDecimal, to: StorageUnit): BigDecimal {
        if (bytes.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

        return if (to.isBit) {
            bytes.multiply(BITS_PER_BYTE, MATH_CONTEXT)
        } else {
            bytes.divide(to.bytesMultiplier, MATH_CONTEXT)
        }
    }
}
