package com.liempo.rmsview.animators

import com.liempo.rmsview.RecognitionBar
import java.util.*

class RmsAnimator(recognitionBars: List<RecognitionBar>) : BarParamsAnimator {
    private val barAnimators: MutableList<BarRmsAnimator>


    init {
        this.barAnimators = ArrayList()
        for (bar in recognitionBars) {
            barAnimators.add(BarRmsAnimator(bar))
        }
    }

    override fun start() {
        for (barAnimator in barAnimators) {
            barAnimator.start()
        }
    }

    override fun stop() {
        for (barAnimator in barAnimators) {
            barAnimator.stop()
        }
    }

    override fun animate() {
        for (barAnimator in barAnimators) {
            barAnimator.animate()
        }
    }

    fun onRmsChanged(rmsDB: Float) {
        for (barAnimator in barAnimators) {
            barAnimator.onRmsChanged(rmsDB)
        }
    }
}

