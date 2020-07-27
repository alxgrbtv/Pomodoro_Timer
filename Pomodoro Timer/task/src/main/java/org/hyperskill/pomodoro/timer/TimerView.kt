package org.hyperskill.pomodoro.timer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import org.hyperskill.pomodoro.R
import org.hyperskill.pomodoro.NotificationHelper
import org.hyperskill.pomodoro.timer.Status.*
import java.util.concurrent.TimeUnit

open class TimerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    var color: Int
        get() = mCirclePaint.color
        set(value) { mCirclePaint.color = value }
    var seconds: Int = WORK.timeInSeconds
        set(value) { field = value.coerceAtLeast(0) }
    var isRunning = false
    var wasRunning = false
    var status = WORK
    private val numberPomodoro = 1
    private var countPomodoro = 0
        set(value) { field = value.coerceAtMost(numberPomodoro) }

    private val startAngle = 270F
    private val thicknessScale = 0.01F
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private lateinit var mCircleOuterBounds: RectF
    private lateinit var mCircleInnerBounds: RectF
    private var mCirclePaint: Paint = Paint()
    private var mEraserPaint: Paint = Paint()
    private var mCircleSweepAngle = 0F
    private var mTimerAnimator: ValueAnimator? = null

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TimerView,
                0, 0).apply {
            try {
                color = getColor(R.styleable.TimerView_color, WORK.color)
            } finally {
                recycle()
            }
        }

        mCirclePaint = Paint()
        mCirclePaint.isAntiAlias = true
        mCirclePaint.color = color
        mEraserPaint = Paint()
        mEraserPaint.isAntiAlias = true
        mEraserPaint.color = Color.TRANSPARENT
        mEraserPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onDraw(canvas: Canvas?) {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR)

        if (mCircleSweepAngle > 0F) {
            mCanvas.drawArc(mCircleOuterBounds, startAngle, mCircleSweepAngle, true, mCirclePaint)
            mCanvas.drawOval(mCircleInnerBounds, mEraserPaint)
        }

        canvas?.drawBitmap(mBitmap, 0F, 0F, mCirclePaint)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        if (width != oldWidth || height != oldHeight) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mBitmap.eraseColor(Color.TRANSPARENT)
            mCanvas = Canvas(mBitmap)
        }
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        updateBounds()
    }

    fun startTimer() {
        resetTimer()
        isRunning = true
        color = WORK.color
        startAnimation()
    }

    fun resetTimer() {
        stopAnimation()
        isRunning = false
        status = WORK
        seconds = WORK.timeInSeconds
        countPomodoro = 0
    }

    private fun startAnimation() {
        mTimerAnimator = ValueAnimator.ofFloat(0F, 1F)
        mTimerAnimator?.duration = TimeUnit.SECONDS
                .toMillis((WORK.timeInSeconds +
                        (REST.timeInSeconds + WORK.timeInSeconds) * numberPomodoro).toLong())
        mTimerAnimator?.interpolator = LinearInterpolator()
        mTimerAnimator?.addUpdateListener { animation ->
            drawProgress(
                    animation.animatedValue as Float
            )
        }
        mTimerAnimator?.start()
    }

    private fun stopAnimation() {
        mTimerAnimator?.cancel()
        mTimerAnimator = null
        drawProgress(0F)
    }

    private fun updateBounds() {
        val thickness = width * thicknessScale
        mCircleOuterBounds = RectF(0F, 0F, width.toFloat(), height.toFloat())
        mCircleInnerBounds = RectF(
                mCircleOuterBounds.left + thickness,
                mCircleOuterBounds.top + thickness,
                mCircleOuterBounds.right - thickness,
                mCircleOuterBounds.bottom - thickness
        )
        invalidate()
    }

    private fun drawProgress(progress: Float) {
        mCircleSweepAngle = 360 * progress
        invalidate()
    }

    @Suppress("DEPRECATION")
    fun createTimer() {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                if (isRunning) {
                    seconds--
                    if (seconds == 0) {
                        handler.post { switchStatus() }
                    }
                }
                updateTime()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updateTime() {
        val minutes = seconds / 60
        val secs = seconds % 60
        text = String.format("%02d:%02d", minutes, secs)
    }

    private fun switchStatus() {
        fun toRest() {
            NotificationHelper.createNotification(context,
                    context.getString(R.string.rest_notif_title),
                    context.getString(R.string.rest_notif_message), true)
            seconds = REST.timeInSeconds
            color = REST.color
            status = REST
        }
        fun toWork() {
            seconds = WORK.timeInSeconds
            color = WORK.color
            status = WORK
            countPomodoro++
            if (countPomodoro == numberPomodoro) {
                status = LAST_WORK
            }
        }
        fun toFinish() {
            NotificationHelper.createNotification(context,
                    context.getString(R.string.finish_notif_title),
                    context.getString(R.string.finish_notif_message), true)
            status = FINISH
            color = FINISH.color
        }

        when (status) {
            WORK -> toRest()
            REST -> toWork()
            LAST_WORK -> toFinish()
            FINISH -> isRunning = false
        }
    }
}
