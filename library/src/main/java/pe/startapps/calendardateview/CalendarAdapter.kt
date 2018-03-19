package pe.startapps.calendardateview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import pe.startapps.calendardateview.extensions.inflate

/**
 * Created by kevin.
 */
class CalendarAdapter(var items: List<CalendarBean>, val dayClickListener: ((CalendarBean) -> Unit)?, var currentDate: CalendarBean) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_calendar_day))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendarBean = items[position]
        with(holder.itemView) {

            tvDay.isSelected = calendarBean.isSameDay(currentDate)

            tvDay.text = calendarBean.dayOfMonth.toString()

            when (calendarBean.offset) {
                1, -1 -> {
                    tvDay.alpha = 0.5f
                    tvDay.isEnabled = false
                }
                else  -> {
                    tvDay.alpha = 1f
                    tvDay.isEnabled = true
                }
            }

            tvDay.setOnClickListener {
                dayClickListener?.invoke(calendarBean)
            }

        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}