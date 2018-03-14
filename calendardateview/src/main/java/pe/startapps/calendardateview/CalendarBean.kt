package pe.startapps.calendardateview

/**
 * Created by kevin.
 */
data class CalendarBean(
        val dayOfMonth: Int,
        val month: Int,
        val year: Int,
        val offset: Int = 0
)