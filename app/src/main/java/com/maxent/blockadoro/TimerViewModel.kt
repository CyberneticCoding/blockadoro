package com.maxent.blockadoro

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.LinkedList

data class Phase(val name: String, val durationInSeconds: Int, var workSessionsBeforeNextPhase: Int, val color: String)
class TimerViewModel: ViewModel() {
    var isTimerRunning = false

    var phases = listOf<Phase>();
    var currentPhaseIndex = 0
    var currentTimeLeft = 0
    val phaseQueue: LinkedList<Phase> = LinkedList()
    init {
        initializePhases()
    }

    private fun initializePhases() {
        val deepFocus = Phase("Deep Focus", 1, 0, "@color/deep")
        val shortBreak = Phase("Short Break", 1, 3, "@color/short")
        val longBreak = Phase("Long Break", 2, 0, "@color/long")
        phases = listOf(deepFocus, shortBreak, longBreak)
        currentPhaseIndex = 0
        currentTimeLeft = deepFocus.durationInSeconds

        repeat(shortBreak.workSessionsBeforeNextPhase) {
            phaseQueue.add(shortBreak)
            phaseQueue.add(deepFocus)
        }
        phaseQueue.add(longBreak)
        phaseQueue.add(deepFocus)
    }

    fun nextPhase() {
        if (phaseQueue.isEmpty()) {
            initializePhases()
        }

        val phase = phaseQueue.poll()

        if (phase != null) {
            currentTimeLeft = phase.durationInSeconds
            timerLiveData.value = currentTimeLeft
            phaseLiveData.value = phase.name
        }
    }

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
}