package com.liempo.rmsview
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.liempo.rmsview.animators.*
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class RmsView : View  {

    private val recognitionBars = ArrayList<RecognitionBar>()
    private var paint: Paint? = null
    private var animator: BarParamsAnimator? = null

    private var radius: Int = 0
    private var spacing: Int = 0
    private var rotationRadius: Int = 0
    private var amplitude: Int = 0

    private var density: Float = 0.toFloat()

    private var animating: Boolean = false

    private var barColor = -1
    private var barColors: IntArray? = null
    private var barMaxHeights: IntArray? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        paint = Paint()
        paint!!.flags = Paint.ANTI_ALIAS_FLAG
        paint!!.color = Color.GRAY

        density = resources.displayMetrics.density

        radius = (CIRCLE_RADIUS_DP * density).toInt()
        spacing = (CIRCLE_SPACING_DP * density).toInt()
        rotationRadius = (ROTATION_RADIUS_DP * density).toInt()
        amplitude = (IDLE_FLOATING_AMPLITUDE_DP * density).toInt()

        if (density <= MDPI_DENSITY) {
            amplitude *= 2
        }
    }

    /** Starts animating view */
    fun play() {
        startIdleInterpolation()
        animating = true
    }

    /** Stops animating view */
    fun stop() {
        if (animator != null) {
            animator!!.stop()
            animator = null
        }
        animating = false
        resetBars()
    }

    /** Starts transform */
    fun transform() {
        startTransformInterpolation()
    }

    fun setRms(rms: Float) {
        if (animator == null || rms < 1f) {
            return
        }
        if (animator !is RmsAnimator) {
            startRmsInterpolation()
        }
        if (animator is RmsAnimator) {
            (animator as RmsAnimator).onRmsChanged(rms)
        }
    }

    /** Set different colors to bars in view
     * @param colors - array with size = [.BARS_COUNT] */
    fun setColors(colors: IntArray?) {
        if (colors == null) return

        barColors = IntArray(BARS_COUNT)
        if (colors.size < BARS_COUNT) {

            System.arraycopy(colors, 0,
                barColors!!, 0, colors.size)

            for (i in colors.size until BARS_COUNT) {
                barColors!![i] = colors[0]
            }
        } else {
            System.arraycopy(colors, 0,
                barColors!!, 0, BARS_COUNT)
        }
    }

    /** Set sizes of bars in view
     * @param heights - array with size = [.BARS_COUNT],
     * if not set uses default bars heights */
    fun setBarMaxHeights(heights: IntArray?) {
        if (heights == null) return

        barMaxHeights = IntArray(BARS_COUNT)
        if (heights.size < BARS_COUNT) {

            System.arraycopy(heights, 0,
                barMaxHeights!!, 0, heights.size)
            for (i in heights.size until BARS_COUNT) {
                barMaxHeights!![i] = heights[0]
            }
        } else {
            System.arraycopy(heights, 0,
                barMaxHeights!!, 0, BARS_COUNT)
        }
    }

    /**Set radius of circle
     * @param radius - Default value = [.CIRCLE_RADIUS_DP] */
    fun setCircleRadius(radius: Int) {
        this.radius = (radius * density).toInt()
    }

    /** Set spacing between circles
     * @param spacing - Default value = [.CIRCLE_SPACING_DP] */
    fun setSpacing(spacing: Int) {
        this.spacing = (spacing * density).toInt()
    }

    /** Set idle animation amplitude
     * @param amplitude - Default value = [.IDLE_FLOATING_AMPLITUDE_DP] */
    fun setIdleStateAmplitude(amplitude: Int) {
        this.amplitude = (amplitude * density).toInt()
    }

    /** Set rotation animation radius
     * @param radius - Default value = [.ROTATION_RADIUS_DP] */
    fun setRotationRadius(radius: Int) {
        this.rotationRadius = (radius * density).toInt()
    }

    override fun onLayout(changed: Boolean, left: Int,
                          top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (recognitionBars.isEmpty()) {
            initBars()
        } else if (changed) {
            recognitionBars.clear()
            initBars()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (recognitionBars.isEmpty()) return

        if (animating)
            animator!!.animate()

        for (i in recognitionBars.indices) {
            val bar = recognitionBars[i]
            if (barColors != null) {
                paint!!.color = barColors!![i]
            } else if (barColor != -1) {
                paint!!.color = barColor
            }
            canvas.drawRoundRect(bar.rect, radius.toFloat(),
                radius.toFloat(), paint!!)
        }

        if (animating) {
            invalidate()
        }
    }

    private fun initBars() {
        val heights = initBarHeights()
        val firstCirclePosition =
                measuredWidth / 2 -
                2 * spacing -
                4 * radius

        for (i in 0 until BARS_COUNT) {
            val x = firstCirclePosition + (2 * radius + spacing) * i
            val bar = RecognitionBar(x, measuredHeight / 2,
                2 * radius, heights[i], radius)
            recognitionBars.add(bar)
        }
    }

    private fun initBarHeights(): List<Int> {
        val barHeights = ArrayList<Int>()
        if (barMaxHeights == null) {
            for (i in 0 until BARS_COUNT) {
                barHeights.add((DEFAULT_BARS_HEIGHT_DP[i]
                        * density).toInt())
            }
        } else {
            for (i in 0 until BARS_COUNT) {
                barHeights.add((barMaxHeights!![i]
                        * density).toInt())
            }
        }

        return barHeights
    }

    fun setCircleRadiusInDp(radius: Int) {
        this.radius = (radius * density).toInt()
    }

    fun setSpacingInDp(spacing: Int) {
        this.spacing = (spacing * density).toInt()
    }

    fun setIdleStateAmplitudeInDp(amplitude: Int) {
        this.amplitude = (amplitude * density).toInt()
    }

    fun setRotationRadiusInDp(radius: Int) {
        this.rotationRadius = (radius * density).toInt()
    }

    private fun resetBars() {
        for (bar in recognitionBars) {
            bar.x = bar.startX
            bar.y = bar.startY
            bar.height = radius * 2
            bar.update()
        }
    }

    fun startIdleInterpolation() {
        animator = IdleAnimator(recognitionBars, amplitude)
        animator!!.start()
    }

    fun startRmsInterpolation() {
        resetBars()
        animator = RmsAnimator(recognitionBars)
        animator!!.start()
    }

    fun startTransformInterpolation() {
        resetBars()
        animator = TransformAnimator(recognitionBars,
            width / 2, height / 2, rotationRadius)
        animator!!.start()
        (animator as TransformAnimator).setOnInterpolationFinishedListener(
            object : TransformAnimator.OnInterpolationFinishedListener {
                override fun onFinished() {
                    startRotateInterpolation()
                }
        })
    }

    fun startRotateInterpolation() {
        animator = RotatingAnimator(recognitionBars,
            width / 2, height / 2)
        animator!!.start()
    }

    companion object {

        const val BARS_COUNT = 5

        private const val CIRCLE_RADIUS_DP = 5
        private const val CIRCLE_SPACING_DP = 11
        private const val ROTATION_RADIUS_DP = 25
        private const val IDLE_FLOATING_AMPLITUDE_DP = 3

        private const val MDPI_DENSITY = 1.5f

        private val DEFAULT_BARS_HEIGHT_DP = intArrayOf(60, 46, 70, 54, 64)
    }
}