package com.example.datastorageconverter.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.datastorageconverter.data.model.AppSettings
import com.example.datastorageconverter.data.model.AppTheme
import com.example.datastorageconverter.data.model.HistoryRecord
import com.example.datastorageconverter.data.model.StorageSystem
import com.example.datastorageconverter.data.model.StorageUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storage_converter_prefs")

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val DEFAULT_MODE = stringPreferencesKey("default_mode")
        val DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val SHOW_COMMON_CONVERSIONS = booleanPreferencesKey("show_common_conversions")
        val HISTORY_JSON = stringPreferencesKey("history_json")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeStr = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
            val theme = try {
                AppTheme.valueOf(themeStr)
            } catch (_: Exception) {
                AppTheme.SYSTEM
            }

            val dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: true

            val modeStr = preferences[PreferencesKeys.DEFAULT_MODE] ?: StorageSystem.DECIMAL.name
            val defaultMode = try {
                StorageSystem.valueOf(modeStr)
            } catch (_: Exception) {
                StorageSystem.DECIMAL
            }

            val unitStr = preferences[PreferencesKeys.DEFAULT_UNIT] ?: StorageUnit.GIGABYTE.name
            val defaultUnit = try {
                StorageUnit.valueOf(unitStr)
            } catch (_: Exception) {
                StorageUnit.GIGABYTE
            }

            val haptics = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true
            val sound = preferences[PreferencesKeys.SOUND_EFFECTS] ?: false
            val showCommon = preferences[PreferencesKeys.SHOW_COMMON_CONVERSIONS] ?: true

            AppSettings(
                theme = theme,
                dynamicColor = dynamicColor,
                defaultMode = defaultMode,
                defaultUnit = defaultUnit,
                hapticFeedback = haptics,
                soundEffects = sound,
                showCommonConversions = showCommon
            )
        }

    val historyFlow: Flow<List<HistoryRecord>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val json = preferences[PreferencesKeys.HISTORY_JSON]
            HistoryJsonSerializer.deserialize(json)
        }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateDefaultMode(system: StorageSystem) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_MODE] = system.name
        }
    }

    suspend fun updateDefaultUnit(unit: StorageUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_UNIT] = unit.name
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun updateSoundEffects(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun updateShowCommonConversions(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMMON_CONVERSIONS] = enabled
        }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.THEME)
            preferences.remove(PreferencesKeys.DYNAMIC_COLOR)
            preferences.remove(PreferencesKeys.DEFAULT_MODE)
            preferences.remove(PreferencesKeys.DEFAULT_UNIT)
            preferences.remove(PreferencesKeys.HAPTIC_FEEDBACK)
            preferences.remove(PreferencesKeys.SOUND_EFFECTS)
            preferences.remove(PreferencesKeys.SHOW_COMMON_CONVERSIONS)
        }
    }

    suspend fun addHistoryRecord(record: HistoryRecord) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.HISTORY_JSON]
            val currentList = HistoryJsonSerializer.deserialize(currentJson)
            // Add to head, filter out any with same ID, trim to max 50 records
            val updated = (listOf(record) + currentList.filter { it.id != record.id }).take(MAX_HISTORY_ITEMS)
            preferences[PreferencesKeys.HISTORY_JSON] = HistoryJsonSerializer.serialize(updated)
        }
    }

    suspend fun deleteHistoryRecord(id: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.HISTORY_JSON]
            val currentList = HistoryJsonSerializer.deserialize(currentJson)
            val updated = currentList.filter { it.id != id }
            preferences[PreferencesKeys.HISTORY_JSON] = HistoryJsonSerializer.serialize(updated)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.HISTORY_JSON)
        }
    }

    companion object {
        const val MAX_HISTORY_ITEMS = 50
    }
}
