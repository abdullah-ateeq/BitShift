package com.example.datastorageconverter.ui.screens.converter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datastorageconverter.data.model.StorageUnit
import com.example.datastorageconverter.ui.components.CommonConversionsCard
import com.example.datastorageconverter.ui.components.ConversionRow
import com.example.datastorageconverter.ui.components.QuickValueChips
import com.example.datastorageconverter.ui.components.SystemSegmentedControl
import com.example.datastorageconverter.ui.components.rememberFeedbackController
import com.example.datastorageconverter.ui.viewmodel.DataStorageConverterViewModel
import kotlinx.coroutines.launch

@Composable
fun ConverterScreen(
    viewModel: DataStorageConverterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val feedback = rememberFeedbackController(
        hapticsEnabled = settings.hapticFeedback,
        soundEnabled = settings.soundEffects
    )

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    val copyToClipboard: (String) -> Unit = { text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Storage Value", text)
        clipboard.setPrimaryClip(clip)
        feedback.performCopyFeedback()
        scope.launch {
            snackbarHostState.showSnackbar("Copied: $text")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isTabletOrLandscape = maxWidth >= 680.dp

            if (isTabletOrLandscape) {
                // Two-column layout for tablets and landscape
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left column: Inputs, Mode, Chips, Actions, Info
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ConverterHeader()
                        Spacer(modifier = Modifier.height(16.dp))
                        SystemSegmentedControl(
                            selectedSystem = uiState.system,
                            onSystemSelected = {
                                feedback.performLightHaptic()
                                viewModel.onSystemChange(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ConverterInputSection(
                            inputValue = uiState.inputValue,
                            selectedUnit = uiState.inputUnit,
                            availableUnits = StorageUnit.unitsFor(uiState.system),
                            errorMessage = uiState.validationError,
                            onValueChange = viewModel::onInputValueChange,
                            onUnitChange = {
                                feedback.performLightHaptic()
                                viewModel.onInputUnitChange(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        QuickValueChips(
                            currentValue = uiState.inputValue,
                            onValueSelect = {
                                feedback.performLightHaptic()
                                viewModel.onQuickValueSelect(it)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ActionButtonsRow(
                            onSave = {
                                feedback.performSaveFeedback()
                                viewModel.onSaveConversion()
                            },
                            onReset = {
                                feedback.performResetFeedback()
                                viewModel.onReset()
                            },
                            onSwap = {
                                feedback.performLightHaptic()
                                viewModel.onSwap()
                            }
                        )
                        if (settings.showCommonConversions) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CommonConversionsCard(
                                onSelectConversion = { value, unit, system ->
                                    feedback.performLightHaptic()
                                    viewModel.onCommonConversionSelect(value, unit, system)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        UnitInformationCard()
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Right column: Prominent summary & Conversion results list
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                    ) {
                        ProminentSummaryCard(
                            inputValueFormatted = uiState.prominentSummary,
                            ratioText = uiState.ratioDescription,
                            systemTitle = uiState.system.title
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.results,
                                key = { it.unit.name }
                            ) { result ->
                                ConversionRow(
                                    result = result,
                                    onCopy = copyToClipboard
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            } else {
                // Single-column layout for portrait phones
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        ConverterHeader()
                    }
                    item {
                        SystemSegmentedControl(
                            selectedSystem = uiState.system,
                            onSystemSelected = {
                                feedback.performLightHaptic()
                                viewModel.onSystemChange(it)
                            }
                        )
                    }
                    item {
                        ConverterInputSection(
                            inputValue = uiState.inputValue,
                            selectedUnit = uiState.inputUnit,
                            availableUnits = StorageUnit.unitsFor(uiState.system),
                            errorMessage = uiState.validationError,
                            onValueChange = viewModel::onInputValueChange,
                            onUnitChange = {
                                feedback.performLightHaptic()
                                viewModel.onInputUnitChange(it)
                            }
                        )
                    }
                    item {
                        QuickValueChips(
                            currentValue = uiState.inputValue,
                            onValueSelect = {
                                feedback.performLightHaptic()
                                viewModel.onQuickValueSelect(it)
                            }
                        )
                    }
                    item {
                        ActionButtonsRow(
                            onSave = {
                                feedback.performSaveFeedback()
                                viewModel.onSaveConversion()
                            },
                            onReset = {
                                feedback.performResetFeedback()
                                viewModel.onReset()
                            },
                            onSwap = {
                                feedback.performLightHaptic()
                                viewModel.onSwap()
                            }
                        )
                    }
                    item {
                        ProminentSummaryCard(
                            inputValueFormatted = uiState.prominentSummary,
                            ratioText = uiState.ratioDescription,
                            systemTitle = uiState.system.title
                        )
                    }
                    // Conversion result items
                    items(
                        items = uiState.results,
                        key = { it.unit.name }
                    ) { result ->
                        ConversionRow(
                            result = result,
                            onCopy = copyToClipboard
                        )
                    }
                    if (settings.showCommonConversions) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            CommonConversionsCard(
                                onSelectConversion = { value, unit, system ->
                                    feedback.performLightHaptic()
                                    viewModel.onCommonConversionSelect(value, unit, system)
                                }
                            )
                        }
                    }
                    item {
                        UnitInformationCard()
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun ConverterHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "BITSHIFT",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Convert digital storage units instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConverterInputSection(
    inputValue: String,
    selectedUnit: StorageUnit,
    availableUnits: List<StorageUnit>,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onUnitChange: (StorageUnit) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Value input field
            OutlinedTextField(
                value = inputValue,
                onValueChange = onValueChange,
                label = { Text("Value") },
                placeholder = { Text("e.g. 1.5") },
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                trailingIcon = {
                    if (inputValue.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear input",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .semantics {
                        contentDescription = "Storage value input"
                    }
            )

            // Unit selector dropdown
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { isDropdownExpanded = true }
                        .semantics {
                            contentDescription = "${selectedUnit.label} unit selected"
                        },
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "UNIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = selectedUnit.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select unit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    availableUnits.forEach { unit ->
                        val isCurrent = unit == selectedUnit
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${unit.symbol} (${unit.label})",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onUnitChange(unit)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onSave: () -> Unit,
    onReset: () -> Unit,
    onSwap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .weight(1.3f)
                .height(42.dp)
                .semantics {
                    contentDescription = "Save conversion"
                }
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SAVE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        OutlinedButton(
            onClick = onSwap,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .semantics {
                    contentDescription = "Swap conversion mode"
                }
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "SWAP",
                style = MaterialTheme.typography.labelMedium
            )
        }

        OutlinedButton(
            onClick = onReset,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .semantics {
                    contentDescription = "Reset values"
                }
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "RESET",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ProminentSummaryCard(
    inputValueFormatted: String,
    ratioText: String,
    systemTitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INPUT SUMMARY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = systemTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = inputValueFormatted,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (ratioText.isNotEmpty()) {
                    Text(
                        text = ratioText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "CONVERTED VALUES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun UnitInformationCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Decimal storage units use multiples of 1,000. Binary units use multiples of 1,024.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
