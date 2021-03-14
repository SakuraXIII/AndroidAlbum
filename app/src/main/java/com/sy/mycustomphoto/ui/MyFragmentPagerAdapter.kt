package com.sy.mycustomphoto.ui

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @program: MyCustomPhoto
 * @description: ViewPager的Fragment适配器
 * @author: SY
 * @create: 2021-03-07 22:27
 **/
class MyFragmentPagerAdapter(
    fm: FragmentManager,
    fragmentList: ArrayList<Fragment> = arrayListOf()
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    // BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 表示只有当Fragment对用户可见时生命周期才执行到onResume setMaxLiftCycle()
    private var fragmentList = arrayListOf<Fragment>()


    init {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addItem(fm: Fragment) {
        fragmentList.add(fm)
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

}