/**
 * Codigo anadido por Felipe Denis
 *
 * Caja negra de estado compartido en memoria entre FocusTimerService y la capa UI.
 */
package com.utdevs.habitflow.service

import com.utdevs.habitflow.domain.model.SessionBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    FINISHED
}

data class TimerUiState(
    val sessionTag: String = "", // "day_1" .. "day_21" o "maintenance"
    val status: TimerStatus = TimerStatus.IDLE,
    val blocks: List<SessionBlock> = emptyList(),
    val currentBlockIndex: Int = 0,
    val remainingMillis: Long = 0L,
    val totalBlockMillis: Long = 0L,
    val finishedTimestampIso: String? = null
) {
    val currentBlock: SessionBlock?
        get() = blocks.getOrNull(currentBlockIndex)
}

class TimerStateHolder {
    private val _state = MutableStateFlow(TimerUiState())
    val state: StateFlow<TimerUiState> = _state.asStateFlow()

    fun update(transform: (TimerUiState) -> TimerUiState) {
        _state.update(transform)
    }

    fun reset() {
        _state.value = TimerUiState()
    }
}
