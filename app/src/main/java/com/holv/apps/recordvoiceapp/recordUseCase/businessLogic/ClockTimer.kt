package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.os.CountDownTimer
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.FireAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.ObtainHolderForActionEvent
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.FinishingPlayback
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class ClockTimer {

    var totalSeconds: Long = 36000 * 10 //  ten hours of recording in seconds
    val intervalSeconds: Long = 1 // one second ticking
    var timeHolder =  AtomicLong(0)
    var seconds = AtomicLong(0)
    var minutes = AtomicLong(0)
    var hours = AtomicLong(0)
    var isPlaying = AtomicBoolean(false)
    var totalSecondsToPlayback = 0L

    //listeners that lives on the holders
    var listener: ObtainHolderForActionEvent? = null
    var fireAnimationListener: FireAnimation? = null
    var finishPlayback: FinishingPlayback? = null

    val countDownTimer =
        object : CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            override fun onTick(millisUntilFinished: Long) {

                if (seconds.get() == 0L) {
                    listener?.onClockTick("00:00")
                    timeHolder.incrementAndGet()
                    seconds.incrementAndGet()
                } else {

                    if (minutes.get() > 59) {
                        seconds.set(0)
                        minutes.set(0)
                        hours.incrementAndGet()
                    }
                    if (seconds.get() > 0 && seconds.get().mod(60) == 0) {
                        minutes.incrementAndGet()
                        seconds.set(0)
                    } else if (seconds.get() > 59) {
                        seconds.set(seconds.get().mod(60).toLong())
                    }

                    val secs = if (seconds.get() > 9) "$seconds" else "0$seconds"
                    val min = if (minutes.get() > 9) "$minutes" else "0$minutes"
                    val hrs = if (hours.get() > 9) "$hours" else "0$hours"

                    if (isPlaying.get()) {
                        if (seconds.get() >= totalSecondsToPlayback || timeHolder.get() >= totalSecondsToPlayback) {
                            isPlaying.set(false)
                            onFinish()
                            cancel()
                            finishPlayback?.onFinishPlayback()
                            listener?.onFinishPlayback()
                        }
                    }

                    if (hours.get() > 0) {
                        listener?.onClockTick("$hrs:$min:$secs")
                    } else {
                        listener?.onClockTick("$min:$secs")
                    }
                    timeHolder.incrementAndGet()
                    seconds.incrementAndGet()
                }
            }

            override fun onFinish() {
                fireAnimationListener?.onFireAnimation(isTurn = false)
            }

        }

    fun resetTimerVariables() {
        seconds.set(0)
        timeHolder.set(0)
        minutes.set(0)
        hours.set(0)
    }

}