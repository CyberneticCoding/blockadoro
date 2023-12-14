package com.maxent.blockadoro

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Phase(val name: String, val durationInSeconds: Int, var workSessionsBeforeNextPhase: Int)
class TimerViewModel: ViewModel() {
    var isTimerRunning = false
    var currentPhaseIndex = 0

    private val deepFocus = Phase("Deep Focus", 1, 0)
    private val shortBreak = Phase("Short Break", 1, 3)
    private val longBreak = Phase("Long Break", 2, 0)
    private val phases = listOf(deepFocus, shortBreak, longBreak)

    private var phasesMutable = phases.map { it.copy() }.toMutableList()

    var currentTimeLeft = deepFocus.durationInSeconds

    private val timerHandler = Handler(Looper.getMainLooper())

    private val updateTimerTask = object : Runnable {
        override fun run() {
            countDownSecond()
            timerHandler.postDelayed(this, 1000)
        }
    }
    val timerLiveData = MutableLiveData<Int>().apply {
        value = currentTimeLeft
    }
    val phaseLiveData = MutableLiveData<String>().apply {
        value = phases[currentPhaseIndex].name
    }

    private fun countDownSecond() {
        if (currentTimeLeft > 0) {
            currentTimeLeft--
            timerLiveData.value = currentTimeLeft
        } else {
            nextPhase()
        }
    }
    fun toggleTimerState() {
        isTimerRunning = !isTimerRunning
        if (isTimerRunning) {
            timerHandler.post(updateTimerTask)
        } else {
            timerHandler.removeCallbacks(updateTimerTask)
        }
    }

    fun nextPhase() {
        if (currentPhaseIndex >= 2) {
            phasesMutable = phases.map { it.copy() }.toMutableList()
            currentPhaseIndex = 0;
        } else {
            // Checks for how many times the phase should appear.
            var sessions = phasesMutable[currentPhaseIndex].workSessionsBeforeNextPhase
            if (sessions > 0) {
                //Go back to the previous phase and remove 1 session
                phasesMutable[currentPhaseIndex].workSessionsBeforeNextPhase--
                currentPhaseIndex--
            } else {
                // If the mutable list has no more sessions left, go to the next phase. Skip empty phases to skip from Deep Work to Long Break
                while (phasesMutable[currentPhaseIndex].workSessionsBeforeNextPhase == 0) {
                    if (currentPhaseIndex >= 2) {
                        break;
                    } else {
                        currentPhaseIndex++
                    }
                }
            }
        }
        val nextPhase = phases[currentPhaseIndex]

        currentTimeLeft = nextPhase.durationInSeconds
        timerLiveData.value = currentTimeLeft
        phaseLiveData.value = nextPhase.name
    }
}