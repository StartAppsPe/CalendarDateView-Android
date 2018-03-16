package pe.startapps.calendardateview

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import pe.startapps.calendardateview.extensions.currentBean
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by kevin.
 */
class CalendarDateView : ViewPager {

    var calendarHeight: Int = 0
    var calendarItemHeight: Int = 0
    var currentSelectPosition: IntArray = intArrayOf(0, 0, 0, 0)

    private var dayColor: Int = Color.GRAY
    private var selectedDayColor: Int = Color.LTGRAY
    private var firstDayOfWeek: Int = 2

    private var dayClickListener: ((CalendarBean) -> Unit)? = null

    private var selectedBean by Delegates.observable(currentBean) { _, oldValue, newValue ->
        calendarViews.values.map { it.adapter as CalendarAdapter }.forEach {
            it.currentDate = newValue
            it.items.forEachIndexed { index, calendarBean ->
                if (calendarBean.isSameDay(oldValue) || calendarBean.isSameDay(newValue)) {
                    it.notifyItemChanged(index)
                }
            }
        }
    }

    private var calendarViews = mutableMapOf<Int, RecyclerView>()

    enum class Mode {
        Week,
        Month
    }

    var mode: Mode by Delegates.observable(Mode.Week) { _, _, _ ->
        calendarViews.clear()
        adapter.notifyDataSetChanged()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarDateView)
        dayColor = a.getInteger(R.styleable.CalendarDateView_calendar_day_color, dayColor)
        selectedDayColor = a.getInteger(R.styleable.CalendarDateView_calendar_selected_day_color, selectedDayColor)
        firstDayOfWeek = a.getInteger(R.styleable.CalendarDateView_calendar_first_day_of_week, firstDayOfWeek)
        a.recycle()
    }

    init {
        setupAdapter()
        setCurrentItem(Integer.MAX_VALUE / 2, false)
        adapter.notifyDataSetChanged()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        calendarHeight = 0
        if (adapter != null) {
            val view = getChildAt(0) as? RecyclerView
            if (view != null) {
                calendarHeight = view.measuredWidth * 6 / 7
                calendarItemHeight = calendarHeight / 6
            }
        }
        setMeasuredDimension(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(calendarHeight, View.MeasureSpec.EXACTLY))

    }

    private fun setupAdapter() {
        adapter = object : PagerAdapter() {

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = createView(position)
                container.addView(view)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
                calendarViews.remove(position)
            }

            override fun isViewFromObject(view: View?, obj: Any) = view == obj

            override fun getItemPosition(obj: Any?) = POSITION_NONE

            override fun getCount() = Int.MAX_VALUE

        }
    }

    private fun createView(position: Int): View {

        val positions = position - Integer.MAX_VALUE / 2

        val calendar = Calendar.getInstance()

        val items = when (mode) {
            Mode.Month -> {
                calendar.add(Calendar.MONTH, positions)
                CalendarFactory.getDaysOfMonth(firstDayOfWeek, calendar)
            }
            Mode.Week  -> {
                calendar.add(Calendar.WEEK_OF_YEAR, positions)
                CalendarFactory.getDaysOfWeek(firstDayOfWeek, calendar)
            }
        }

        val calendarAdapter = CalendarAdapter(items, dayClickListener, selectedBean)

        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = GridLayoutManager(context, 7)
        recyclerView.adapter = calendarAdapter
        recyclerView.itemAnimator.changeDuration = 0
        recyclerView.hasFixedSize()

        calendarViews[position] = recyclerView

        return recyclerView
    }

    fun setOnDayClickListener(listener: ((CalendarBean) -> Unit)) {
        dayClickListener = {
            listener.invoke(it)
            selectedBean = it
        }
    }

    fun toggle() {
        mode = when (mode) {
            Mode.Month -> Mode.Week
            Mode.Week  -> Mode.Month
        }
    }

}