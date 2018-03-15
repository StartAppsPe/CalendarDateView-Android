package pe.startapps.calendardateview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by kevin.
 */
class CalendarLayout : FrameLayout {

    lateinit var topView: CalendarDateView
    lateinit var contentView: ViewGroup

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        topView = getChildAt(0) as CalendarDateView
        contentView = getChildAt(1) as ViewGroup

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        contentView.offsetTopAndBottom(196)
    }

    override fun computeScroll() {
        super.computeScroll()
        Log.e(":)", ":)")
    }

}