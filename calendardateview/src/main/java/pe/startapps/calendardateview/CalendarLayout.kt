package pe.startapps.calendardateview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.OverScroller

/**
 * Created by kevin.
 */
class CalendarLayout : FrameLayout {

    lateinit var calendarView: CalendarDateView
    lateinit var contentView: ViewGroup

    lateinit var scroller: OverScroller

    private var calendarHeight = 0
    private var calendarItemHeight = 0
    private var maxDistance = 0
    private var bottomViewTopHeight = 0

    private var maxVelocity = 0.0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    init {

        val vc = ViewConfiguration.get(context)
        maxVelocity = vc.scaledMaximumFlingVelocity.toFloat()

        scroller = OverScroller(context)
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

        bottomViewTopHeight = when (calendarView.mode) {
            CalendarDateView.Mode.Month -> calendarHeight
            CalendarDateView.Mode.Week  -> calendarItemHeight
        }
        contentView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec) - calendarItemHeight, View.MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        contentView.offsetTopAndBottom(bottomViewTopHeight)
        Log.e(":)", "onLayout")
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.e(":)", "onInterceptTouchEvent")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e(":)", "onTouchEvent")
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        super.computeScroll()
        Log.e(":)", ":)")
    }

}