package org.hyperskill.pomodoro.timer

import android.graphics.Color

enum class Status(val color: Int, var timeInSeconds: Int) {
    WORK(Color.RED, 1500),
    REST(Color.GREEN, 300),
    LAST_WORK(Color.RED, 1500),
    FINISH(Color.YELLOW, 0)
}