package com.example.datastorageconverter.data.model

import java.math.BigDecimal

enum class StorageUnit(
    val label: String,
    val symbol: String,
    val system: StorageSystem?,
    val isBit: Boolean,
    val bytesMultiplier: BigDecimal
) {
    BIT(
        label = "Bit",
        symbol = "b",
        system = null,
        isBit = true,
        bytesMultiplier = BigDecimal.ONE // 1 Bit = 1/8 Byte handled in converter
    ),
    BYTE(
        label = "Byte",
        symbol = "B",
        system = null,
        isBit = false,
        bytesMultiplier = BigDecimal("1")
    ),
    // Decimal (SI) Units - Multiples of 1,000
    KILOBYTE(
        label = "Kilobyte",
        symbol = "KB",
        system = StorageSystem.DECIMAL,
        isBit = false,
        bytesMultiplier = BigDecimal("1000")
    ),
    MEGABYTE(
        label = "Megabyte",
        symbol = "MB",
        system = StorageSystem.DECIMAL,
        isBit = false,
        bytesMultiplier = BigDecimal("1000000")
    ),
    GIGABYTE(
        label = "Gigabyte",
        symbol = "GB",
        system = StorageSystem.DECIMAL,
        isBit = false,
        bytesMultiplier = BigDecimal("1000000000")
    ),
    TERABYTE(
        label = "Terabyte",
        symbol = "TB",
        system = StorageSystem.DECIMAL,
        isBit = false,
        bytesMultiplier = BigDecimal("1000000000000")
    ),
    PETABYTE(
        label = "Petabyte",
        symbol = "PB",
        system = StorageSystem.DECIMAL,
        isBit = false,
        bytesMultiplier = BigDecimal("1000000000000000")
    ),
    // Binary (IEC) Units - Multiples of 1,024
    KIBIBYTE(
        label = "Kibibyte",
        symbol = "KiB",
        system = StorageSystem.BINARY,
        isBit = false,
        bytesMultiplier = BigDecimal("1024")
    ),
    MEBIBYTE(
        label = "Mebibyte",
        symbol = "MiB",
        system = StorageSystem.BINARY,
        isBit = false,
        bytesMultiplier = BigDecimal("1048576")
    ),
    GIBIBYTE(
        label = "Gibibyte",
        symbol = "GiB",
        system = StorageSystem.BINARY,
        isBit = false,
        bytesMultiplier = BigDecimal("1073741824")
    ),
    TEBIBYTE(
        label = "Tebibyte",
        symbol = "TiB",
        system = StorageSystem.BINARY,
        isBit = false,
        bytesMultiplier = BigDecimal("1099511627776")
    ),
    PEBIBYTE(
        label = "Pebibyte",
        symbol = "PiB",
        system = StorageSystem.BINARY,
        isBit = false,
        bytesMultiplier = BigDecimal("1125899906842624")
    );

    fun correspondingUnit(targetSystem: StorageSystem): StorageUnit {
        return when (this) {
            BIT -> BIT
            BYTE -> BYTE
            KILOBYTE -> if (targetSystem == StorageSystem.BINARY) KIBIBYTE else KILOBYTE
            MEGABYTE -> if (targetSystem == StorageSystem.BINARY) MEBIBYTE else MEGABYTE
            GIGABYTE -> if (targetSystem == StorageSystem.BINARY) GIBIBYTE else GIGABYTE
            TERABYTE -> if (targetSystem == StorageSystem.BINARY) TEBIBYTE else TERABYTE
            PETABYTE -> if (targetSystem == StorageSystem.BINARY) PEBIBYTE else PETABYTE

            KIBIBYTE -> if (targetSystem == StorageSystem.DECIMAL) KILOBYTE else KIBIBYTE
            MEBIBYTE -> if (targetSystem == StorageSystem.DECIMAL) MEGABYTE else MEBIBYTE
            GIBIBYTE -> if (targetSystem == StorageSystem.DECIMAL) GIGABYTE else GIBIBYTE
            TEBIBYTE -> if (targetSystem == StorageSystem.DECIMAL) TERABYTE else TEBIBYTE
            PEBIBYTE -> if (targetSystem == StorageSystem.DECIMAL) PETABYTE else PEBIBYTE
        }
    }

    companion object {
        fun unitsFor(system: StorageSystem): List<StorageUnit> {
            return when (system) {
                StorageSystem.DECIMAL -> listOf(
                    BIT,
                    BYTE,
                    KILOBYTE,
                    MEGABYTE,
                    GIGABYTE,
                    TERABYTE,
                    PETABYTE
                )
                StorageSystem.BINARY -> listOf(
                    BIT,
                    BYTE,
                    KIBIBYTE,
                    MEBIBYTE,
                    GIBIBYTE,
                    TEBIBYTE,
                    PEBIBYTE
                )
            }
        }
    }
}
