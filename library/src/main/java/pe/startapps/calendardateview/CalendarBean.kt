package pe.startapps.calendardateview

/**
 * Created by kevin.
 */
data class CalendarBean(
        val dayOfMonth: Int,
        val month: Int,
        val year: Int,
        val offset: Int = 0
) {

    fun isSameDay(other: CalendarBean): Boolean {
        return year == other.year &&
                month == other.month &&
                dayOfMonth == other.dayOfMonth
    }

}