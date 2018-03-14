package pe.startapps.calendardateview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import pe.startapps.calendardateview.extensions.inflate

/**
 * Created by kevin.
 */
class CalendarAdapter(val items: List<CalendarBean>, val dayClickListener: ((CalendarBean) -> Unit)?) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_calendar_day))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], dayClickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(calendarBean: CalendarBean, dayClickListener: ((CalendarBean) -> Unit)?) {
            with(itemView) {
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

    }

}