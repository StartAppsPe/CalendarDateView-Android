package pe.startapps.calendardateview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.OverScroller
import kotlin.math.absoluteValue

/**
 * Created by kevin.
 */
class CalendarLayout : FrameLayout {

    lateinit var calendarView: CalendarDateView
    lateinit var contentView: ViewGroup

    private var calendarHeight = 0
    private var calendarItemHeight = 0
    private var maxDistance = 0
    private var bottomViewTopHeight = 0

    private var maxVelocity = 0.0f
    private val interpolator = Interpolator { t1 ->
        var t2 = t1
        t2 -= 1.0f
        t2 * t2 * t2 * t2 * t2 + 1.0f
    }

    private var scroller: OverScroller

    private var state = State.COLLAPSED

    private enum class State {

        COLLAPSING,
        COLLAPSED,
        EXPANDING,
        EXPANDED

    }


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    init {
        val vc = ViewConfiguration.get(context)
        maxVelocity = vc.scaledMaximumFlingVelocity.toFloat()
        scroller = OverScroller(context, interpolator)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        calendarView = getChildAt(0) as CalendarDateView
        contentView = getChildAt(1) as ViewGroup

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        calendarHeight = calendarView.calendarHeight
        calendarItemHeight = calendarView.calendarItemHeight
        maxDistance = calendarHeight - calendarItemHeight

        bottomViewTopHeight = when (state) {
            State.EXPANDED, State.COLLAPSING -> calendarHeight
            else                             -> calendarItemHeight
        }

        contentView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec) - calendarItemHeight, View.MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        when (state) {
            State.EXPANDING -> {
                calendarView.offsetTopAndBottom(-calendarView.getSelectedOffset())
                contentView.post { startScroll(contentView.top, calendarHeight) }
            }
            else            -> {
                contentView.offsetTopAndBottom(bottomViewTopHeight)
            }
        }
    }

    /**
     *
     */

    private var oldY = 0
    private var isScrolling = false

    override fun computeScroll() {
        super.computeScroll()
        bottomViewTopHeight = contentView.top
        if (scroller.computeScrollOffset()) {
            isScrolling = true
            move(scroller.currY - oldY)
            oldY = scroller.currY
            postInvalidate()
        } else {
            isScrolling = false
            oldY = 0
            when (state) {
                State.COLLAPSING -> {
                    state = State.COLLAPSED
                    calendarView.mode = CalendarDateView.Mode.Week
                }
                State.EXPANDING  -> state = State.EXPANDED
                else             -> {
                }
            }
        }
    }

    private fun move(dy: Int) {

        val offset = calendarView.getSelectedOffset()

        val dy1 = calculateDy(calendarView.top, dy, -offset, 0)
        val dy2 = calculateDy(contentView.top - calendarHeight, dy, -maxDistance, 0)

        if (dy1 != 0) calendarView.offsetTopAndBottom(dy1)
        if (dy2 != 0) contentView.offsetTopAndBottom(dy2)

    }

    private fun calculateDy(top: Int, dy: Int, minValue: Int, maxValue: Int) = when {
        top + dy < minValue -> minValue - top
        top + dy > maxValue -> maxValue - top
        else                -> dy
    }

    private fun startScroll(starty: Int, endY: Int) {
        val distance = endY - starty
        val duration = distance.toFloat() / maxDistance * 400
        scroller.startScroll(0, 0, 0, distance, duration.absoluteValue.toInt())
        postInvalidate()
    }

    fun toggle() {
        when (state) {
            State.COLLAPSED, State.COLLAPSING -> {
                state = State.EXPANDING
                calendarView.mode = CalendarDateView.Mode.Month
            }
            State.EXPANDED, State.EXPANDING   -> {
                state = State.COLLAPSING
                startScroll(contentView.top, calendarHeight - maxDistance)
            }
        }
    }

    private fun expand() {
        state = State.EXPANDING
        calendarView.mode = CalendarDateView.Mode.Month
    }

    private fun collapse() {
        state = State.COLLAPSING
        startScroll(contentView.top, calendarHeight - maxDistance)
    }

    /**
     *
     */

    private var oy: Float = 0f
    private var ox: Float = 0f

    private var velocityTracker: VelocityTracker? = null
    private var potionerId: Int = 0

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        var isflag = false

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {

                oy = ev.y
                ox = ev.x

                cancel()
                potionerId = ev.getPointerId(0)

                when (state) {
                    State.COLLAPSED, State.COLLAPSING -> {
                        state = State.EXPANDING
                    }
                    State.EXPANDED, State.EXPANDING   -> {
                        state = State.COLLAPSING
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {

                val y = ev.y
                val x = ev.x

                val xdiff = x - ox
                val ydiff = y - oy

                if (Math.abs(ydiff) > 5 && Math.abs(ydiff) > Math.abs(xdiff)) {

                    isflag = true

                    if (ydiff > 0) {
                        if (state == State.EXPANDED) {
                            return super.onInterceptTouchEvent(ev)
                        }
                    } else {
                        if (state == State.COLLAPSED) {
                            return super.onInterceptTouchEvent(ev)
                        }
                    }

                }

                ox = x
                oy = y

            }
            MotionEvent.ACTION_UP   -> {

            }
        }

        return isScrolling || isflag || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        processTouchEvent(ev)
        return true
    }

    private fun processTouchEvent(ev: MotionEvent) {

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN   -> {

            }
            MotionEvent.ACTION_MOVE   -> {

                if (isScrolling) {
                    return
                }
                val cy = ev.y
                val dy = (cy - oy).toInt()

                if (dy == 0) {
                    return
                }
                oy = cy
                move(dy)

            }
            MotionEvent.ACTION_UP     -> {

                if (isScrolling) {
                    cancel()
                    return
                }

                val pointerId = potionerId
                velocityTracker!!.computeCurrentVelocity(1000, maxVelocity)
                val crrentV = velocityTracker!!.getYVelocity(pointerId)

                if (Math.abs(crrentV) > 2000) {
                    if (crrentV > 0) {
                        expand()
                    } else {
                        collapse()
                    }
                    cancel()
                    return
                }

                val top = contentView.top - calendarHeight
                val maxd = maxDistance


                if (Math.abs(top) < maxd / 2) {
                    expand()
                } else {
                    collapse()
                }
                cancel()

            }
            MotionEvent.ACTION_CANCEL -> {
                cancel()
            }
        }

    }

    private fun cancel() {
        velocityTracker?.recycle()
        velocityTracker = null
    }

}