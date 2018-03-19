package pe.startapps.calendardateview.extensions

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pe.startapps.calendardateview.CalendarBean
import java.util.*
import kotlin.math.floor

/**
 * Created by kevin.
 */


internal val currentBean get() = Calendar.getInstance().let { CalendarBean(it.dayOfMonth, it.month, it.year) }

internal fun CalendarBean.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

// Calendar Extensions

internal val Calendar.year get() = get(Calendar.YEAR)
internal val Calendar.month get() = get(Calendar.MONTH)
internal val Calendar.dayOfMonth get() = get(Calendar.DAY_OF_MONTH)
internal val Calendar.dayOfWeek get() = get(Calendar.DAY_OF_WEEK)
internal val Calendar.lastDayOfMonth get() = getActualMaximum(Calendar.DAY_OF_MONTH)

internal fun Calendar.copy(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = time
    return calendar
}

internal fun Calendar.adjust(startDayOfWeek: Int = 1) = apply {
    add(Calendar.DATE, -startDayOfWeek + 1)
}

internal fun Calendar.startWeek(startDayOfWeek: Int = 1) = apply {
    add(Calendar.DATE, -startDayOfWeek + 1)
    set(Calendar.DAY_OF_WEEK, startDayOfWeek)
}

internal fun Calendar.midnight() = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

internal fun Calendar.firstDayWeekOfMonth(): Int {
    set(Calendar.DAY_OF_MONTH, 1)
    return dayOfWeek
}

internal fun Calendar.dateByAddingMonths(months: Int): Calendar {
    add(Calendar.MONTH, months)
    return this
}

internal fun Calendar.intervalOfWeeks(endDate: Calendar): Int {
    val interval = (endDate.midnight().timeInMillis - midnight().timeInMillis)
    val intervalDays = interval / (1000 * 60 * 60 * 24)
    Log.e(";(", "$intervalDays $time ${endDate.time} ")
    return floor(intervalDays.toFloat() / 7).toInt()
}

// View Extensions

internal fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}