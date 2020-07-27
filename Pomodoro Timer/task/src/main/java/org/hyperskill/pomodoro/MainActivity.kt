package org.hyperskill.pomodoro

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.hyperskill.pomodoro.timer.Status
import org.hyperskill.pomodoro.timer.TimerView


class MainActivity : AppCompatActivity(), SettingDialogFragment.SettingDialogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerView = findViewById<TimerView>(R.id.timerView)

        if (savedInstanceState != null) {
            timerView.seconds = savedInstanceState.getInt("seconds")
            timerView.isRunning = savedInstanceState.getBoolean("isRunning")
            timerView.wasRunning = savedInstanceState.getBoolean("wasRunning")
        }

        timerView.createTimer()

        NotificationHelper.createNotificationChannel(this,
                NotificationManagerCompat.IMPORTANCE_DEFAULT,
                getString(R.string.app_name), "App notification channel.")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("seconds", timerView.seconds)
        savedInstanceState.putBoolean("isRunning", timerView.isRunning)
        savedInstanceState.putBoolean("wasRunning", timerView.wasRunning)
    }

    override fun onResume() {
        super.onResume()
        if (timerView.wasRunning) {
            timerView.isRunning = true
        }
    }

    override fun onPause() {
        super.onPause()
        timerView.wasRunning = timerView.isRunning
        timerView.isRunning = false
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickStart(view: View) {
        timerView.startTimer()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickReset(view: View) {
        timerView.resetTimer()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickSetting(view: View) {
        showSettingDialog()
    }

    private fun showSettingDialog() {
        val settingDialogFragment = SettingDialogFragment()
        settingDialogFragment.show(supportFragmentManager, "setting")
    }

    override fun onDialogPositiveClick(newWorkTime: Int?, newRestTime: Int?) {
        Status.WORK.timeInSeconds = newWorkTime ?: Status.WORK.timeInSeconds
        Status.REST.timeInSeconds = newRestTime ?: Status.REST.timeInSeconds
        timerView.resetTimer()
    }
}
