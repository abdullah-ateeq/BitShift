package com.example.datastorageconverter.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

class FeedbackController(
    private val view: View,
    private val hapticsEnabled: Boolean,
    private val soundEnabled: Boolean
) {
    fun performLightHaptic() {
        if (hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun performSuccessHaptic() {
        if (hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        }
    }

    fun performCopyFeedback() {
        if (hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        if (soundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    fun performSaveFeedback() {
        if (hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        }
        if (soundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    fun performResetFeedback() {
        if (hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }
}

@Composable
fun rememberFeedbackController(
    hapticsEnabled: Boolean,
    soundEnabled: Boolean
): FeedbackController {
    val view = LocalView.current
    return remember(view, hapticsEnabled, soundEnabled) {
        FeedbackController(view, hapticsEnabled, soundEnabled)
    }
}
