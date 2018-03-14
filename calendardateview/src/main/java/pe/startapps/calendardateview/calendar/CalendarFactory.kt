package pe.startapps.calendardateview.calendar

import pe.startapps.calendardateview.utils.*
import java.util.*

/**
 * Created by kevin.
 */
object CalendarFactory {

    fun getDaysOfMonth(firstDayOfWeek: Int, currentMonth: Calendar): List<CalendarBean> {

        val prevMonth = currentMonth.copy().dateByAddingMonths(-1)
        val nextMonth = currentMonth.copy().dateByAddingMonths(1)

        // Current Month

        val currentDays = (1..currentMonth.lastDayOfMonth)
                .map { CalendarBean(it, currentMonth.month, currentMonth.year) }

        val firstDayWeek = currentMonth.copy().firstDayWeekOfMonth()

        // Prev Month

        val daysOfPrevMonth = when {
            firstDayWeek < firstDayOfWeek -> 7 - firstDayOfWeek + firstDayWeek
            else                          -> firstDayWeek - firstDayOfWeek
        }

        val prevDays = (prevMonth.lastDayOfMonth downTo prevMonth.lastDayOfMonth - daysOfPrevMonth + 1)
                .map { CalendarBean(it, prevMonth.month, prevMonth.year, -1) }
                .reversed()

        // Next Month

        val daysOfNextMonth = 42 - (prevDays.size + currentDays.size)

        val nextDays = (1..daysOfNextMonth)
                .map { CalendarBean(it, nextMonth.month, nextMonth.year, 1) }

        val days = mutableListOf<CalendarBean>()
        days += prevDays
        days += currentDays
        days += nextDays

        return days
    }

    fun getDaysOfWeek(firstDayOfWeek: Int, currentWeek: Calendar): List<CalendarBean> {

        val week = currentWeek.copy()
                .apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeek) }

        return (0 until 7)
                .map { week.copy().apply { add(Calendar.DAY_OF_WEEK, it) } }
                .map { CalendarBean(it.dayOfMonth, it.month, it.year, 0) }
    }

}