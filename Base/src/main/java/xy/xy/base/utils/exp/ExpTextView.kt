package xy.xy.base.utils.exp

import android.view.View
import android.widget.TextView

/**
 * 设置未读数
 */
fun TextView.setUnReadNumber(unreadSize: Long){
    this.visibility = if (unreadSize <= 0) View.INVISIBLE else View.VISIBLE
    this.text =  if (unreadSize <= 99) "$unreadSize" else "99+"
}