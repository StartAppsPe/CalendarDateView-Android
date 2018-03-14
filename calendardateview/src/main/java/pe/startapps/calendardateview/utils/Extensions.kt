package pe.startapps.calendardateview.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by kevin.
 */

// View Extensions

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

// Calendar Extensions

val Calendar.year get() = get(Calendar.YEAR)
val Calendar.month get() = get(Calendar.MONTH)
val Calendar.weekOfYear get() = get(Calendar.WEEK_OF_YEAR)
val Calendar.dayOfYear get() = get(Calendar.DAY_OF_YEAR)
val Calendar.dayOfMonth get() = get(Calendar.DAY_OF_MONTH)
val Calendar.dayOfWeek get() = get(Calendar.DAY_OF_WEEK)
val Calendar.hour get() = get(Calendar.HOUR)
val Calendar.hourOfDay get() = get(Calendar.HOUR_OF_DAY)
val Calendar.minute get() = get(Calendar.MINUTE)
val Calendar.second get() = get(Calendar.SECOND)
val Calendar.lastDayOfMonth get() = getActualMaximum(Calendar.DAY_OF_MONTH)



fun Calendar.copy(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = time
    return calendar
}

fun Calendar.firstDayWeekOfMonth(): Int {
    set(Calendar.DAY_OF_MONTH, 1)
    return dayOfWeek
}

fun Calendar.lastDayWeekOfMonth(): Int {
    set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
    return dayOfWeek
}