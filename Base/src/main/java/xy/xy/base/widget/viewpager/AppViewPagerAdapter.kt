package xy.xy.base.widget.viewpager

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * viewpage分页效果
 */
class AppViewPagerAdapter(val list: List<View>) : PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (list.size > position) container.removeView(list[position]) // 删除页卡
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(list[position], 0) // 添加页卡
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

}