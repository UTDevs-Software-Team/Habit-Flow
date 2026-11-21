/**
 * Codigo anadido por Felipe Denis
 *
 * Controlador sensorial Black Box:
 * - Vibracion corta (45ms) cada vez que el usuario obtiene puntos (+XP).
 * - Vibracion larga (patron ascendente) + tono opcional al subir de nivel de meta o rango.
 */
package com.utdevs.habitflow.data.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticAndToneController(
    private val context: Context
) {

    private fun getVibrator(): Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    /** Vibracion corta cada vez que el usuario gana puntos (+XP). */
    fun triggerPointsGainHaptic(hapticsEnabled: Boolean = true) {
        if (!hapticsEnabled) return
        runCatching {
            val vibrator = getVibrator() ?: return
            vibrator.vibrate(VibrationEffect.createOneShot(45L, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    /** Vibracion larga + tono sutil cuando sube un nivel/etapa de la meta o Rango Botanico. */
    fun triggerLevelUpCelebration(soundEnabled: Boolean, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) {
            runCatching {
                val vibrator = getVibrator()
                val timings = longArrayOf(0L, 120L, 60L, 280L)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        }
        if (soundEnabled) {
            runCatching {
                val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 65)
                tone.startTone(ToneGenerator.TONE_PROP_ACK, 300)
            }
        }
    }
}
