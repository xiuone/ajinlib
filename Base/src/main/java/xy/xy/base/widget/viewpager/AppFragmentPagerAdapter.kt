package xy.xy.base.widget.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class AppFragmentPagerAdapter(fm: FragmentManager, val framentList: List<Fragment>, private val titleList:Array<String>?=null) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return framentList[position]
    }


    override fun getCount(): Int {
        return framentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when {
            titleList != null && position < titleList.size -> {
                titleList[position]
            }
            else -> {
                super.getPageTitle(position)
            }
        }
    }
}