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

    private var dayColor: Int = Color.GRAY
    private var selectedDayColor: Int = Color.LTGRAY
    private var firstDayOfWeek: Int = 2

    private var dayClickListener: ((CalendarBean) -> Unit)? = null

    private var selectedBean by Delegates.observable(currentBean) { _, oldValue, newValue ->
        calendarAdapters.values.forEach {
            it.currentDate = newValue
            it.items.forEachIndexed { index, calendarBean ->
                if (calendarBean.isSameDay(oldValue) || calendarBean.isSameDay(newValue)) {
                    it.notifyItemChanged(index)
                }
            }
        }
    }

    private var calendarAdapters = mutableMapOf<Int, CalendarAdapter>()

    enum class Mode {
        Week,
        Month
    }

    var mode: Mode by Delegates.observable(Mode.Week) { _, _, _ ->
        calendarAdapters.clear()
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

        val childHeight = widthMeasureSpec * 6 / 7

        (0 until childCount).map { getChildAt(it) }.forEach { child ->
            child.measure(widthMeasureSpec, childHeight)
        }

        val heightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, heightSpec)
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
                calendarAdapters.remove(position)
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

        calendarAdapters[position] = calendarAdapter

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