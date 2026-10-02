package com.example.datastorageconverter.data.model

enum class AppTheme(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val defaultMode: StorageSystem = StorageSystem.DECIMAL,
    val defaultUnit: StorageUnit = StorageUnit.GIGABYTE,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = false,
    val showCommonConversions: Boolean = true
)
