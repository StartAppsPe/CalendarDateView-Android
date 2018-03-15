package pe.startapps.calendardateview.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pe.startapps.calendardateview.CalendarBean
import java.util.*

/**
 * Created by kevin.
 */


internal val currentBean get() = Calendar.getInstance().let { CalendarBean(it.dayOfMonth, it.month, it.year) }

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

internal fun Calendar.firstDayWeekOfMonth(): Int {
    set(Calendar.DAY_OF_MONTH, 1)
    return dayOfWeek
}

internal fun Calendar.dateByAddingMonths(months: Int): Calendar {
    add(Calendar.MONTH, months)
    return this
}

// View Extensions

internal fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}