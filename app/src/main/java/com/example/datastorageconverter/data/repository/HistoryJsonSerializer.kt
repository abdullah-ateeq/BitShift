package com.example.datastorageconverter.data.repository

import com.example.datastorageconverter.data.model.HistoryRecord
import com.example.datastorageconverter.data.model.StorageSystem
import com.example.datastorageconverter.data.model.StorageUnit
import java.util.UUID

object HistoryJsonSerializer {

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun unescape(s: String): String {
        return s.replace("\\\"", "\"")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\\", "\\")
    }

    fun serialize(records: List<HistoryRecord>): String {
        val sb = StringBuilder()
        sb.append("[")
        records.forEachIndexed { i, record ->
            if (i > 0) sb.append(",")
            sb.append("{")
            sb.append("\"id\":\"").append(escape(record.id)).append("\",")
            sb.append("\"timestamp\":").append(record.timestamp).append(",")
            sb.append("\"system\":\"").append(record.system.name).append("\",")
            sb.append("\"inputValue\":\"").append(escape(record.inputValue)).append("\",")
            sb.append("\"inputUnit\":\"").append(record.inputUnit.name).append("\",")
            sb.append("\"formattedInput\":\"").append(escape(record.formattedInput)).append("\",")
            sb.append("\"primaryResultFormatted\":\"").append(escape(record.primaryResultFormatted)).append("\",")
            sb.append("\"secondaryResults\":[")
            record.secondaryResults.forEachIndexed { j, (k, v) ->
                if (j > 0) sb.append(",")
                sb.append("{\"k\":\"").append(escape(k)).append("\",\"v\":\"").append(escape(v)).append("\"}")
            }
            sb.append("]")
            sb.append("}")
        }
        sb.append("]")
        return sb.toString()
    }

    fun deserialize(jsonString: String?): List<HistoryRecord> {
        if (jsonString.isNullOrBlank() || jsonString == "[]") return emptyList()

        return try {
            val list = mutableListOf<HistoryRecord>()
            // Find individual object blocks between top-level brackets
            val trimmed = jsonString.trim()
            if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()

            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            if (inner.isEmpty()) return emptyList()

            // Split by objects: each record starts with {"id":
            val objectRegex = Regex("""\{"id":.*?"secondaryResults":\[.*?\]\}""")
            val matches = objectRegex.findAll(inner)

            for (match in matches) {
                val objStr = match.value
                val id = extractStringField(objStr, "id") ?: UUID.randomUUID().toString()
                val timestamp = extractLongField(objStr, "timestamp") ?: System.currentTimeMillis()
                val systemStr = extractStringField(objStr, "system") ?: StorageSystem.DECIMAL.name
                val system = try {
                    StorageSystem.valueOf(systemStr)
                } catch (_: Exception) {
                    StorageSystem.DECIMAL
                }
                val inputValue = extractStringField(objStr, "inputValue") ?: "1"
                val unitStr = extractStringField(objStr, "inputUnit") ?: StorageUnit.GIGABYTE.name
                val inputUnit = try {
                    StorageUnit.valueOf(unitStr)
                } catch (_: Exception) {
                    StorageUnit.GIGABYTE
                }
                val formattedInput = extractStringField(objStr, "formattedInput") ?: ""
                val primaryResult = extractStringField(objStr, "primaryResultFormatted") ?: ""

                // Extract secondary results
                val secondaryPairs = mutableListOf<Pair<String, String>>()
                val secRegex = Regex("""\{"k":"(.*?)","v":"(.*?)"\}""")
                val secMatches = secRegex.findAll(objStr)
                for (sec in secMatches) {
                    val k = unescape(sec.groupValues[1])
                    val v = unescape(sec.groupValues[2])
                    secondaryPairs.add(Pair(k, v))
                }

                list.add(
                    HistoryRecord(
                        id = id,
                        timestamp = timestamp,
                        system = system,
                        inputValue = inputValue,
                        inputUnit = inputUnit,
                        formattedInput = formattedInput,
                        primaryResultFormatted = primaryResult,
                        secondaryResults = secondaryPairs
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun extractStringField(obj: String, field: String): String? {
        val regex = Regex(""""$field":"(.*?)"""")
        val match = regex.find(obj) ?: return null
        return unescape(match.groupValues[1])
    }

    private fun extractLongField(obj: String, field: String): Long? {
        val regex = Regex(""""$field":(\d+)""")
        val match = regex.find(obj) ?: return null
        return match.groupValues[1].toLongOrNull()
    }
}
