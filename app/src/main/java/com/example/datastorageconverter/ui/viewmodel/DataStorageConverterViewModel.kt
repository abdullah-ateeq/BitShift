package com.example.datastorageconverter.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.datastorageconverter.data.converter.StorageConverter
import com.example.datastorageconverter.data.formatter.StorageFormatter
import com.example.datastorageconverter.data.model.AppSettings
import com.example.datastorageconverter.data.model.AppTheme
import com.example.datastorageconverter.data.model.ConversionResult
import com.example.datastorageconverter.data.model.HistoryRecord
import com.example.datastorageconverter.data.model.StorageSystem
import com.example.datastorageconverter.data.model.StorageUnit
import com.example.datastorageconverter.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class ConverterUiState(
    val inputValue: String = "1",
    val inputUnit: StorageUnit = StorageUnit.GIGABYTE,
    val system: StorageSystem = StorageSystem.DECIMAL,
    val validationError: String? = null,
    val results: List<ConversionResult> = emptyList(),
    val prominentSummary: String = "1 GB",
    val ratioDescription: String = "",
    val userFeedbackMessage: String? = null
)

class DataStorageConverterViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application.applicationContext)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val history: StateFlow<List<HistoryRecord>> = repository.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    init {
        // Compute initial conversion state
        computeConversions(
            valueStr = _uiState.value.inputValue,
            unit = _uiState.value.inputUnit,
            system = _uiState.value.system
        )
    }

    fun onInputValueChange(newInput: String) {
        // Strip out unwanted non-numeric characters while allowing valid decimal inputs
        val sanitized = newInput.filter { it.isDigit() || it == '.' || it == '-' }
        _uiState.update { it.copy(inputValue = sanitized) }
        computeConversions(
            valueStr = sanitized,
            unit = _uiState.value.inputUnit,
            system = _uiState.value.system
        )
    }

    fun onSystemChange(newSystem: StorageSystem) {
        if (_uiState.value.system == newSystem) return

        // Switch to corresponding unit for target system (e.g. GB -> GiB)
        val currentUnit = _uiState.value.inputUnit
        val updatedUnit = currentUnit.correspondingUnit(newSystem)

        _uiState.update {
            it.copy(
                system = newSystem,
                inputUnit = updatedUnit
            )
        }
        computeConversions(
            valueStr = _uiState.value.inputValue,
            unit = updatedUnit,
            system = newSystem
        )
    }

    fun onInputUnitChange(newUnit: StorageUnit) {
        _uiState.update { it.copy(inputUnit = newUnit) }
        computeConversions(
            valueStr = _uiState.value.inputValue,
            unit = newUnit,
            system = _uiState.value.system
        )
    }

    fun onQuickValueSelect(value: String) {
        _uiState.update { it.copy(inputValue = value) }
        computeConversions(
            valueStr = value,
            unit = _uiState.value.inputUnit,
            system = _uiState.value.system
        )
    }

    fun onCommonConversionSelect(value: String, unit: StorageUnit, system: StorageSystem) {
        _uiState.update {
            it.copy(
                inputValue = value,
                inputUnit = unit,
                system = system
            )
        }
        computeConversions(
            valueStr = value,
            unit = unit,
            system = system
        )
    }

    fun onSwap() {
        val current = _uiState.value
        // Secondary convenience swap: toggle between Decimal and Binary or common units
        val targetSystem = if (current.system == StorageSystem.DECIMAL) StorageSystem.BINARY else StorageSystem.DECIMAL
        onSystemChange(targetSystem)
    }

    fun onReset() {
        val defaultMode = settings.value.defaultMode
        val defaultUnit = settings.value.defaultUnit.correspondingUnit(defaultMode)
        _uiState.update {
            it.copy(
                inputValue = "1",
                inputUnit = defaultUnit,
                system = defaultMode,
                validationError = null,
                userFeedbackMessage = "Reset to default values"
            )
        }
        computeConversions(
            valueStr = "1",
            unit = defaultUnit,
            system = defaultMode
        )
    }

    fun onSaveConversion() {
        val state = _uiState.value
        if (state.validationError != null || state.results.isEmpty()) return

        val primaryResult = state.results.firstOrNull { !it.isSelectedInput }
            ?: state.results.firstOrNull()
        val primaryFormatted = primaryResult?.formattedWithSymbol ?: state.prominentSummary

        val secondaryList = state.results.map {
            Pair(it.unit.symbol, it.formattedValue)
        }

        val record = HistoryRecord(
            system = state.system,
            inputValue = state.inputValue,
            inputUnit = state.inputUnit,
            formattedInput = state.prominentSummary,
            primaryResultFormatted = primaryFormatted,
            secondaryResults = secondaryList
        )

        viewModelScope.launch {
            repository.addHistoryRecord(record)
            _uiState.update { it.copy(userFeedbackMessage = "Conversion saved to history") }
        }
    }

    fun onRestoreHistory(record: HistoryRecord) {
        _uiState.update {
            it.copy(
                system = record.system,
                inputValue = record.inputValue,
                inputUnit = record.inputUnit,
                userFeedbackMessage = "Restored: ${record.formattedInput}"
            )
        }
        computeConversions(
            valueStr = record.inputValue,
            unit = record.inputUnit,
            system = record.system
        )
    }

    fun onDeleteHistory(id: String) {
        viewModelScope.launch {
            repository.deleteHistoryRecord(id)
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.update { it.copy(userFeedbackMessage = "History cleared") }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    // Settings actions
    fun updateTheme(theme: AppTheme) = viewModelScope.launch { repository.updateTheme(theme) }
    fun updateDynamicColor(enabled: Boolean) = viewModelScope.launch { repository.updateDynamicColor(enabled) }
    fun updateDefaultMode(system: StorageSystem) = viewModelScope.launch { repository.updateDefaultMode(system) }
    fun updateDefaultUnit(unit: StorageUnit) = viewModelScope.launch { repository.updateDefaultUnit(unit) }
    fun updateHapticFeedback(enabled: Boolean) = viewModelScope.launch { repository.updateHapticFeedback(enabled) }
    fun updateSoundEffects(enabled: Boolean) = viewModelScope.launch { repository.updateSoundEffects(enabled) }
    fun updateShowCommonConversions(enabled: Boolean) = viewModelScope.launch { repository.updateShowCommonConversions(enabled) }
    fun resetSettings() = viewModelScope.launch { repository.resetSettings() }

    private fun computeConversions(valueStr: String, unit: StorageUnit, system: StorageSystem) {
        val trimmed = valueStr.trim()

        if (trimmed.isEmpty()) {
            _uiState.update {
                it.copy(
                    validationError = "Enter a storage value.",
                    results = emptyList(),
                    prominentSummary = "-- ${unit.symbol}",
                    ratioDescription = ""
                )
            }
            return
        }

        if (trimmed.startsWith("-")) {
            _uiState.update {
                it.copy(
                    validationError = "Enter a value of 0 or greater.",
                    results = emptyList(),
                    prominentSummary = "-- ${unit.symbol}",
                    ratioDescription = ""
                )
            }
            return
        }

        val parsedBigDecimal = try {
            BigDecimal(trimmed)
        } catch (_: Exception) {
            _uiState.update {
                it.copy(
                    validationError = "Enter a valid number.",
                    results = emptyList(),
                    prominentSummary = "-- ${unit.symbol}",
                    ratioDescription = ""
                )
            }
            return
        }

        if (parsedBigDecimal < BigDecimal.ZERO) {
            _uiState.update {
                it.copy(
                    validationError = "Enter a value of 0 or greater.",
                    results = emptyList(),
                    prominentSummary = "-- ${unit.symbol}",
                    ratioDescription = ""
                )
            }
            return
        }

        val unitsForSystem = StorageUnit.unitsFor(system)
        val conversionResults = unitsForSystem.map { targetUnit ->
            val converted = StorageConverter.convert(parsedBigDecimal, unit, targetUnit)
            val formatted = StorageFormatter.formatValue(converted)
            val formattedWithSymbol = StorageFormatter.formatWithSymbol(converted, targetUnit)
            ConversionResult(
                unit = targetUnit,
                rawValue = converted,
                formattedValue = formatted,
                formattedWithSymbol = formattedWithSymbol,
                isSelectedInput = (targetUnit == unit)
            )
        }

        val prominentSummary = StorageFormatter.formatWithSymbol(parsedBigDecimal, unit)

        // Show secondary ratio insight if applicable (e.g. 1.5 GB = 0.0015 TB or 1,500 MB)
        val ratioDesc = buildRatioDescription(parsedBigDecimal, unit, system)

        _uiState.update {
            it.copy(
                validationError = null,
                results = conversionResults,
                prominentSummary = prominentSummary,
                ratioDescription = ratioDesc
            )
        }
    }

    private fun buildRatioDescription(value: BigDecimal, unit: StorageUnit, system: StorageSystem): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) return ""
        val targetUnit = when (unit) {
            StorageUnit.GIGABYTE -> StorageUnit.TERABYTE
            StorageUnit.TERABYTE -> StorageUnit.PETABYTE
            StorageUnit.MEGABYTE -> StorageUnit.GIGABYTE
            StorageUnit.KILOBYTE -> StorageUnit.MEGABYTE
            StorageUnit.GIBIBYTE -> StorageUnit.TEBIBYTE
            StorageUnit.TEBIBYTE -> StorageUnit.PEBIBYTE
            StorageUnit.MEBIBYTE -> StorageUnit.GIBIBYTE
            StorageUnit.KIBIBYTE -> StorageUnit.MEBIBYTE
            StorageUnit.BYTE -> if (system == StorageSystem.DECIMAL) StorageUnit.KILOBYTE else StorageUnit.KIBIBYTE
            StorageUnit.BIT -> StorageUnit.BYTE
            else -> null
        } ?: return ""

        val converted = StorageConverter.convert(value, unit, targetUnit)
        return "= ${StorageFormatter.formatWithSymbol(converted, targetUnit)}"
    }
}
