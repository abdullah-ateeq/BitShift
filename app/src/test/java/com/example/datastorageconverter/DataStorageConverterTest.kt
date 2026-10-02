package com.example.datastorageconverter

import com.example.datastorageconverter.data.converter.StorageConverter
import com.example.datastorageconverter.data.formatter.StorageFormatter
import com.example.datastorageconverter.data.model.HistoryRecord
import com.example.datastorageconverter.data.model.StorageSystem
import com.example.datastorageconverter.data.model.StorageUnit
import com.example.datastorageconverter.data.repository.HistoryJsonSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class DataStorageConverterTest {

    // 1. Byte → KB decimal
    @Test
    fun test1_byteToKilobyteDecimal() {
        val result = StorageConverter.convert(
            BigDecimal("1000"),
            StorageUnit.BYTE,
            StorageUnit.KILOBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))
    }

    // 2. KB → MB decimal
    @Test
    fun test2_kilobyteToMegabyteDecimal() {
        val result = StorageConverter.convert(
            BigDecimal("1000"),
            StorageUnit.KILOBYTE,
            StorageUnit.MEGABYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))
    }

    // 3. MB → GB decimal
    @Test
    fun test3_megabyteToGigabyteDecimal() {
        val result = StorageConverter.convert(
            BigDecimal("1000"),
            StorageUnit.MEGABYTE,
            StorageUnit.GIGABYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        // Also verify 1 MB = 1,000,000 Bytes
        val bytes = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.MEGABYTE,
            StorageUnit.BYTE
        )
        assertEquals(0, BigDecimal("1000000").compareTo(bytes))
    }

    // 4. GB → TB decimal
    @Test
    fun test4_gigabyteToTerabyteDecimal() {
        val result = StorageConverter.convert(
            BigDecimal("1000"),
            StorageUnit.GIGABYTE,
            StorageUnit.TERABYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        // 1 GB = 1,000 MB
        val mb = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.GIGABYTE,
            StorageUnit.MEGABYTE
        )
        assertEquals(0, BigDecimal("1000").compareTo(mb))
    }

    // 5. TB → PB decimal
    @Test
    fun test5_terabyteToPetabyteDecimal() {
        val result = StorageConverter.convert(
            BigDecimal("1000"),
            StorageUnit.TERABYTE,
            StorageUnit.PETABYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))
    }

    // 6. Byte → KiB binary
    @Test
    fun test6_byteToKibibyteBinary() {
        val result = StorageConverter.convert(
            BigDecimal("1024"),
            StorageUnit.BYTE,
            StorageUnit.KIBIBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))
    }

    // 7. KiB → MiB binary
    @Test
    fun test7_kibibyteToMebibyteBinary() {
        val result = StorageConverter.convert(
            BigDecimal("1024"),
            StorageUnit.KIBIBYTE,
            StorageUnit.MEBIBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        // 1 MiB = 1,048,576 Bytes
        val bytes = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.MEBIBYTE,
            StorageUnit.BYTE
        )
        assertEquals(0, BigDecimal("1048576").compareTo(bytes))
    }

    // 8. MiB → GiB binary
    @Test
    fun test8_mebibyteToGibibyteBinary() {
        val result = StorageConverter.convert(
            BigDecimal("1024"),
            StorageUnit.MEBIBYTE,
            StorageUnit.GIBIBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        // 1 GiB = 1,024 MiB
        val mib = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.GIBIBYTE,
            StorageUnit.MEBIBYTE
        )
        assertEquals(0, BigDecimal("1024").compareTo(mib))
    }

    // 9. GiB → TiB binary
    @Test
    fun test9_gibibyteToTebibyteBinary() {
        val result = StorageConverter.convert(
            BigDecimal("1024"),
            StorageUnit.GIBIBYTE,
            StorageUnit.TEBIBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        // 1 TiB = 1,024 GiB
        val gib = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.TEBIBYTE,
            StorageUnit.GIBIBYTE
        )
        assertEquals(0, BigDecimal("1024").compareTo(gib))
    }

    // 10. TiB → PiB binary
    @Test
    fun test10_tebibyteToPebibyteBinary() {
        val result = StorageConverter.convert(
            BigDecimal("1024"),
            StorageUnit.TEBIBYTE,
            StorageUnit.PEBIBYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))
    }

    // 11. Bit → Byte
    @Test
    fun test11_bitToByte() {
        val result = StorageConverter.convert(
            BigDecimal("8"),
            StorageUnit.BIT,
            StorageUnit.BYTE
        )
        assertEquals(0, BigDecimal("1").compareTo(result))

        val halfByte = StorageConverter.convert(
            BigDecimal("4"),
            StorageUnit.BIT,
            StorageUnit.BYTE
        )
        assertEquals(0, BigDecimal("0.5").compareTo(halfByte))
    }

    // 12. Byte → Bit
    @Test
    fun test12_byteToBit() {
        val result = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.BYTE,
            StorageUnit.BIT
        )
        assertEquals(0, BigDecimal("8").compareTo(result))
    }

    // 13. Decimal vs binary differences
    @Test
    fun test13_decimalVsBinaryDifferences() {
        val decimalBytes = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.GIGABYTE,
            StorageUnit.BYTE
        )
        val binaryBytes = StorageConverter.convert(
            BigDecimal("1"),
            StorageUnit.GIBIBYTE,
            StorageUnit.BYTE
        )

        // 1 GB = 1,000,000,000 Bytes
        assertEquals(0, BigDecimal("1000000000").compareTo(decimalBytes))
        // 1 GiB = 1,073,741,824 Bytes
        assertEquals(0, BigDecimal("1073741824").compareTo(binaryBytes))

        // 1 GiB is strictly greater than 1 GB by 73,741,824 bytes (~7.37%)
        val difference = binaryBytes.subtract(decimalBytes)
        assertEquals(0, BigDecimal("73741824").compareTo(difference))
        assertTrue(binaryBytes > decimalBytes)
    }

    // 14. Zero value
    @Test
    fun test14_zeroValue() {
        val result = StorageConverter.convert(
            BigDecimal.ZERO,
            StorageUnit.GIGABYTE,
            StorageUnit.BYTE
        )
        assertEquals(0, BigDecimal.ZERO.compareTo(result))
        assertEquals("0", StorageFormatter.formatValue(result))
    }

    // 15. Decimal values
    @Test
    fun test15_decimalValues() {
        // 1.5 GB to MB
        val mbResult = StorageConverter.convert(
            BigDecimal("1.5"),
            StorageUnit.GIGABYTE,
            StorageUnit.MEGABYTE
        )
        assertEquals(0, BigDecimal("1500").compareTo(mbResult))
        assertEquals("1,500", StorageFormatter.formatValue(mbResult))

        // 1.5 GB to KB
        val kbResult = StorageConverter.convert(
            BigDecimal("1.5"),
            StorageUnit.GIGABYTE,
            StorageUnit.KILOBYTE
        )
        assertEquals(0, BigDecimal("1500000").compareTo(kbResult))
        assertEquals("1,500,000", StorageFormatter.formatValue(kbResult))

        // 1.5 GB to TB
        val tbResult = StorageConverter.convert(
            BigDecimal("1.5"),
            StorageUnit.GIGABYTE,
            StorageUnit.TERABYTE
        )
        assertEquals(0, BigDecimal("0.0015").compareTo(tbResult))
        assertEquals("0.0015", StorageFormatter.formatValue(tbResult))

        // 1.5 GB to Bits
        val bitsResult = StorageConverter.convert(
            BigDecimal("1.5"),
            StorageUnit.GIGABYTE,
            StorageUnit.BIT
        )
        assertEquals(0, BigDecimal("12000000000").compareTo(bitsResult))
        assertEquals("12,000,000,000", StorageFormatter.formatValue(bitsResult))
    }

    // 16. Large values
    @Test
    fun test16_largeValues() {
        val result = StorageConverter.convert(
            BigDecimal("100"),
            StorageUnit.PETABYTE,
            StorageUnit.TERABYTE
        )
        assertEquals(0, BigDecimal("100000").compareTo(result))
        assertEquals("100,000", StorageFormatter.formatValue(result))
    }

    // 17. Invalid negative input
    @Test
    fun test17_invalidNegativeInput() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            StorageConverter.convert(
                BigDecimal("-5"),
                StorageUnit.GIGABYTE,
                StorageUnit.MEGABYTE
            )
        }
        assertEquals("Enter a value of 0 or greater.", exception.message)
    }

    // 18. Unit switching
    @Test
    fun test18_unitSwitching() {
        // Decimal -> Binary
        assertEquals(StorageUnit.GIBIBYTE, StorageUnit.GIGABYTE.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.MEBIBYTE, StorageUnit.MEGABYTE.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.KIBIBYTE, StorageUnit.KILOBYTE.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.TEBIBYTE, StorageUnit.TERABYTE.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.PEBIBYTE, StorageUnit.PETABYTE.correspondingUnit(StorageSystem.BINARY))

        // Binary -> Decimal
        assertEquals(StorageUnit.GIGABYTE, StorageUnit.GIBIBYTE.correspondingUnit(StorageSystem.DECIMAL))
        assertEquals(StorageUnit.MEGABYTE, StorageUnit.MEBIBYTE.correspondingUnit(StorageSystem.DECIMAL))
        assertEquals(StorageUnit.KILOBYTE, StorageUnit.KIBIBYTE.correspondingUnit(StorageSystem.DECIMAL))
        assertEquals(StorageUnit.TERABYTE, StorageUnit.TEBIBYTE.correspondingUnit(StorageSystem.DECIMAL))
        assertEquals(StorageUnit.PETABYTE, StorageUnit.PEBIBYTE.correspondingUnit(StorageSystem.DECIMAL))

        // Shared units remain identical
        assertEquals(StorageUnit.BIT, StorageUnit.BIT.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.BYTE, StorageUnit.BYTE.correspondingUnit(StorageSystem.BINARY))
        assertEquals(StorageUnit.BIT, StorageUnit.BIT.correspondingUnit(StorageSystem.DECIMAL))
        assertEquals(StorageUnit.BYTE, StorageUnit.BYTE.correspondingUnit(StorageSystem.DECIMAL))
    }

    // 19. Formatter behavior
    @Test
    fun test19_formatterBehavior() {
        assertEquals("0", StorageFormatter.formatValue(BigDecimal("0")))
        assertEquals("1", StorageFormatter.formatValue(BigDecimal("1.000000")))
        assertEquals("1.5", StorageFormatter.formatValue(BigDecimal("1.500000")))
        assertEquals("1,024", StorageFormatter.formatValue(BigDecimal("1024")))
        assertEquals("1,500,000", StorageFormatter.formatValue(BigDecimal("1500000")))
        assertEquals("0.0015", StorageFormatter.formatValue(BigDecimal("0.0015000")))

        // Ensure no floating point precision artifacts like 1.5000000000000002
        assertNotEquals("1.5000000000000002", StorageFormatter.formatValue(BigDecimal("1.5")))
        assertEquals("1.5", StorageFormatter.formatValue(BigDecimal("1.5")))
    }

    // 20. History serialization
    @Test
    fun test20_historySerialization() {
        val records = listOf(
            HistoryRecord(
                id = "test-1",
                timestamp = 1727800000000L,
                system = StorageSystem.DECIMAL,
                inputValue = "1.50",
                inputUnit = StorageUnit.GIGABYTE,
                formattedInput = "1.5 GB",
                primaryResultFormatted = "1,500 MB",
                secondaryResults = listOf(Pair("MB", "1,500"), Pair("KB", "1,500,000"))
            ),
            HistoryRecord(
                id = "test-2",
                timestamp = 1727800060000L,
                system = StorageSystem.BINARY,
                inputValue = "512",
                inputUnit = StorageUnit.MEBIBYTE,
                formattedInput = "512 MiB",
                primaryResultFormatted = "524,288 KiB",
                secondaryResults = listOf(Pair("KiB", "524,288"), Pair("Bytes", "536,870,912"))
            )
        )

        val json = HistoryJsonSerializer.serialize(records)
        assertTrue(json.contains("test-1"))
        assertTrue(json.contains("1.50"))
        assertTrue(json.contains("GIGABYTE"))
        assertTrue(json.contains("512 MiB"))

        val deserialized = HistoryJsonSerializer.deserialize(json)
        assertEquals(2, deserialized.size)

        val first = deserialized[0]
        assertEquals("test-1", first.id)
        assertEquals(StorageSystem.DECIMAL, first.system)
        assertEquals("1.50", first.inputValue)
        assertEquals(StorageUnit.GIGABYTE, first.inputUnit)
        assertEquals("1.5 GB", first.formattedInput)
        assertEquals("1,500 MB", first.primaryResultFormatted)
        assertEquals(2, first.secondaryResults.size)
        assertEquals("MB", first.secondaryResults[0].first)
        assertEquals("1,500", first.secondaryResults[0].second)

        val second = deserialized[1]
        assertEquals("test-2", second.id)
        assertEquals(StorageSystem.BINARY, second.system)
        assertEquals("512", second.inputValue)
        assertEquals(StorageUnit.MEBIBYTE, second.inputUnit)
    }
}
