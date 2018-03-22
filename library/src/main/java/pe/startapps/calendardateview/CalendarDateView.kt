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
import pe.startapps.calendardateview.extensions.*
import java.util.*
import kotlin.math.floor
import kotlin.properties.Delegates

/**
 * Created by kevin.
 */
class CalendarDateView : ViewPager {

    var calendarHeight: Int = 0
    var calendarItemHeight: Int = 0

    private var dayColor: Int = Color.GRAY
    private var selectedDayColor: Int = Color.LTGRAY
    private var firstDayOfWeek: Int = 2

    private val numOfPages = 1000
    private val initialPosition = numOfPages / 2

    private var dayClickListener: ((CalendarBean) -> Unit)? = null

    private var calendarViews = mutableMapOf<Int, RecyclerView>()

    enum class Mode {
        Week,
        Month
    }

    var mode: Mode by Delegates.observable(Mode.Week) { _, oldValue, _ ->
        onCalendarModeChanged(oldValue)
    }

    var selectedBean by Delegates.observable(currentBean) { _, oldValue, newValue ->
        onSelectedBeanChanged(oldValue, newValue)
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
        setCurrentItem(initialPosition, false)
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

            override fun getCount() = numOfPages

        }
    }

    private fun createView(position: Int): View {

        val calendarAdapter = CalendarAdapter(getItems(position), dayClickListener, selectedBean)

        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = GridLayoutManager(context, 7)
        recyclerView.adapter = calendarAdapter
        recyclerView.itemAnimator.changeDuration = 0
        recyclerView.hasFixedSize()

        calendarViews[position] = recyclerView

        return recyclerView
    }

    private fun getItems(position: Int): List<CalendarBean> {

        val positions = position - initialPosition

        val calendar = Calendar.getInstance()

        return when (mode) {
            Mode.Month -> {
                calendar.add(Calendar.MONTH, positions)
                CalendarFactory.getDaysOfMonth(firstDayOfWeek, calendar)
            }
            Mode.Week  -> {
                calendar.add(Calendar.WEEK_OF_YEAR, positions)
                calendar.add(Calendar.DATE, -firstDayOfWeek + 1)
                CalendarFactory.getDaysOfWeek(firstDayOfWeek, calendar)
            }
        }
    }

    private fun getSelectedPosition(): Int {
        var selectePosition = 0
        calendarViews[currentItem]?.let { recyclerView ->
            (recyclerView.adapter as? CalendarAdapter)?.let {
                it.items.forEachIndexed { index, bean ->
                    if (bean.isSameDay(selectedBean)) {
                        selectePosition = index
                    }
                }
            }
        }
        return selectePosition
    }

    private fun onCalendarModeChanged(oldValue: Mode) {

        var positions = 0

        when (oldValue) {
            Mode.Month -> {
                (calendarViews[currentItem]?.adapter as? CalendarAdapter)?.let {

                    val bean = it.items.find { it.isSameDay(selectedBean) } ?: it.items.first()

                    val startBean = currentBean.toCalendar().midnight().startWeek(firstDayOfWeek)
                    val endBean = bean.toCalendar().midnight().startWeek(firstDayOfWeek)

                    val diffWeeks = startBean.intervalOfWeeks(endBean)

                    positions = diffWeeks

                }
            }
            Mode.Week  -> {
                (calendarViews[currentItem]?.adapter as? CalendarAdapter)?.let {

                    val bean = it.items.find { it.isSameDay(selectedBean) } ?: it.items.first()

                    val startBean = currentBean.toCalendar().midnight().startMonth()
                    val endBean = bean.toCalendar().midnight().startMonth()

                    val diffMonths = startBean.intervalOfMonth(endBean)

                    positions = diffMonths

                }
            }
        }

        setCurrentItem(initialPosition + positions, false)
        adapter.notifyDataSetChanged()

        /*calendarViews.entries.forEach { (key, recyclerView) ->
            (recyclerView.adapter as? CalendarAdapter)?.let {
                it.items = getItems(key)
                it.notifyDataSetChanged()
            }
        }*/

    }

    private fun onSelectedBeanChanged(oldValue: CalendarBean, newValue: CalendarBean) {
        calendarViews.values.map { it.adapter as CalendarAdapter }.forEach {
            it.currentDate = newValue
            it.items.forEachIndexed { index, calendarBean ->
                if (calendarBean.isSameDay(oldValue) || calendarBean.isSameDay(newValue)) {
                    it.notifyItemChanged(index)
                }
            }
        }
    }

    fun setOnDayClickListener(listener: ((CalendarBean) -> Unit)) {
        dayClickListener = {
            listener.invoke(it)
            selectedBean = it
        }
    }

    fun getSelectedOffset(): Int {
        return floor(getSelectedPosition().toFloat() / 7)
                .times(calendarItemHeight)
                .toInt()
    }

}