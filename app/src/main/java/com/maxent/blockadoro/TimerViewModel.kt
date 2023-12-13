package com.maxent.blockadoro

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    var currentTimeLeft = 2 // 25 minutes in seconds
    var isTimerRunning = false
    var currentPhase = "Deep Focus"

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
        value = currentPhase
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
        phaseLiveData.value = "Short Break"
        currentTimeLeft = 300 // 5 min in seconds
        timerLiveData.value = currentTimeLeft
    }
}