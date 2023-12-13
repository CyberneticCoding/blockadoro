package com.maxent.blockadoro

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    var currentTimeLeft = 1500 // 25 minutes

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

    fun countDownSecond() {
        if (currentTimeLeft > 0) {
            currentTimeLeft--
            timerLiveData.value = currentTimeLeft
        }
    }
    fun pauseTime() {
        timerHandler.removeCallbacks(updateTimerTask)
    }
    fun resumeTime() {
        timerHandler.post(updateTimerTask)
    }
}