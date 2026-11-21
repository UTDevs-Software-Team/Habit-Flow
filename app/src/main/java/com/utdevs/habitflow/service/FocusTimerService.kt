/**
 * Codigo anadido por Felipe Denis
 *
 * ForegroundService del temporizador multi-bloque.
 * Usa SystemClock.elapsedRealtime() para mantener precision exacta con pantalla apagada
 * y encadena automaticamente bloques FOCUS y BREAK.
 */
package com.utdevs.habitflow.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.utdevs.habitflow.HabitFlowApp
import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.SessionBlock
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusTimerService : Service() {

    companion object {
        const val ACTION_START = "com.utdevs.habitflow.action.START"
        const val ACTION_PAUSE = "com.utdevs.habitflow.action.PAUSE"
        const val ACTION_RESUME = "com.utdevs.habitflow.action.RESUME"
        const val ACTION_SKIP_TO_5S = "com.utdevs.habitflow.action.SKIP_5S"
        const val ACTION_STOP = "com.utdevs.habitflow.action.STOP"

        const val EXTRA_SESSION_TAG = "extra_session_tag"
        const val EXTRA_BLOCK_TYPES = "extra_block_types"
        const val EXTRA_BLOCK_MINUTES = "extra_block_minutes"

        fun startSession(
            context: Context,
            sessionTag: String,
            blocks: List<SessionBlock>
        ) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SESSION_TAG, sessionTag)
                putExtra(EXTRA_BLOCK_TYPES, blocks.map { it.type.name }.toTypedArray())
                putExtra(EXTRA_BLOCK_MINUTES, blocks.map { it.durationMinutes }.toIntArray())
            }
            context.startForegroundService(intent)
        }

        fun sendAction(context: Context, action: String) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickerJob: Job? = null
    private var targetElapsedRealtime: Long = 0L

    private val stateHolder: TimerStateHolder by lazy {
        (application as HabitFlowApp).container.timerStateHolder
    }
    private val focusShield: com.utdevs.habitflow.data.shield.FocusShieldController by lazy {
        (application as HabitFlowApp).container.focusShieldController
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        TimerNotificationHelper.ensureChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_PAUSE -> handlePause()
            ACTION_RESUME -> handleResume()
            ACTION_SKIP_TO_5S -> handleSkipTo5s()
            ACTION_STOP -> handleStop()
        }
        return START_NOT_STICKY
    }

    private fun handleStart(intent: Intent) {
        val tag = intent.getStringExtra(EXTRA_SESSION_TAG).orEmpty()
        val types = intent.getStringArrayExtra(EXTRA_BLOCK_TYPES) ?: emptyArray()
        val mins = intent.getIntArrayExtra(EXTRA_BLOCK_MINUTES) ?: intArrayOf()

        val blocks = types.indices.map { i ->
            val type = if (types[i] == BlockType.BREAK.name) BlockType.BREAK else BlockType.FOCUS
            SessionBlock(type = type, durationMinutes = mins.getOrElse(i) { 5 })
        }
        if (blocks.isEmpty()) {
            stopSelf()
            return
        }

        val firstMillis = blocks.first().durationMinutes * 60_000L
        stateHolder.update {
            TimerUiState(
                sessionTag = tag,
                status = TimerStatus.RUNNING,
                blocks = blocks,
                currentBlockIndex = 0,
                remainingMillis = firstMillis,
                totalBlockMillis = firstMillis,
                finishedTimestampIso = null
            )
        }

        startForeground(
            TimerNotificationHelper.NOTIFICATION_ID,
            TimerNotificationHelper.buildOngoingNotification(this, stateHolder.state.value)
        )
        applyShieldForBlock(blocks.first().type)
        startTicker(firstMillis)
    }

    private fun handlePause() {
        val current = stateHolder.state.value
        if (current.status != TimerStatus.RUNNING) return
        tickerJob?.cancel()
        focusShield.restorePreviousDnd()
        val remaining = (targetElapsedRealtime - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        stateHolder.update {
            it.copy(status = TimerStatus.PAUSED, remainingMillis = remaining)
        }
        updateNotification()
    }

    private fun handleResume() {
        val current = stateHolder.state.value
        if (current.status != TimerStatus.PAUSED) return
        stateHolder.update { it.copy(status = TimerStatus.RUNNING) }
        current.currentBlock?.let { applyShieldForBlock(it.type) }
        startTicker(current.remainingMillis)
    }

    private fun handleSkipTo5s() {
        val current = stateHolder.state.value
        if (current.status != TimerStatus.RUNNING && current.status != TimerStatus.PAUSED) return
        val fiveSeconds = 5_000L
        stateHolder.update {
            it.copy(status = TimerStatus.RUNNING, remainingMillis = fiveSeconds)
        }
        startTicker(fiveSeconds)
    }

    private fun handleStop() {
        tickerJob?.cancel()
        focusShield.restorePreviousDnd()
        stateHolder.reset()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTicker(durationMillis: Long) {
        tickerJob?.cancel()
        targetElapsedRealtime = SystemClock.elapsedRealtime() + durationMillis
        tickerJob = serviceScope.launch {
            while (isActive) {
                val remaining = (targetElapsedRealtime - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                stateHolder.update { it.copy(remainingMillis = remaining) }
                updateNotification()

                if (remaining <= 0L) {
                    onBlockFinished()
                    break
                }
                delay(250L)
            }
        }
    }

    private fun onBlockFinished() {
        emitSoftTransitionAlert()
        val current = stateHolder.state.value
        val nextIndex = current.currentBlockIndex + 1

        if (nextIndex < current.blocks.size) {
            val nextBlock = current.blocks[nextIndex]
            val nextBlockMillis = nextBlock.durationMinutes * 60_000L
            stateHolder.update {
                it.copy(
                    currentBlockIndex = nextIndex,
                    remainingMillis = nextBlockMillis,
                    totalBlockMillis = nextBlockMillis
                )
            }
            applyShieldForBlock(nextBlock.type)
            startTicker(nextBlockMillis)
        } else {
            focusShield.restorePreviousDnd()
            stateHolder.update {
                it.copy(
                    status = TimerStatus.FINISHED,
                    remainingMillis = 0L,
                    finishedTimestampIso = Instant.now().toString()
                )
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun applyShieldForBlock(type: BlockType) {
        if (type == BlockType.FOCUS) {
            focusShield.activateFocusDnd()
        } else {
            focusShield.restorePreviousDnd()
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(
            TimerNotificationHelper.NOTIFICATION_ID,
            TimerNotificationHelper.buildOngoingNotification(this, stateHolder.state.value)
        )
    }

    private fun emitSoftTransitionAlert() {
        runCatching {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(220L, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(220L, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    override fun onDestroy() {
        tickerJob?.cancel()
        focusShield.restorePreviousDnd()
        serviceScope.cancel()
        super.onDestroy()
    }
}
