package pe.startapps.calendardateview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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

    private var scroller: OverScroller

    private var state = State.COLLAPSED

    private var oldY = 0
    private var isScrolling = false

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

        scroller = OverScroller(context, AccelerateDecelerateInterpolator())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        calendarView = getChildAt(0) as CalendarDateView
        contentView = getChildAt(1) as ViewGroup

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(":)", "onMeasure")
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
        Log.e(":)", "onLayout")
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

}