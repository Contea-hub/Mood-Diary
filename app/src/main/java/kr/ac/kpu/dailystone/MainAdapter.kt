package kr.ac.kpu.dailystone

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class MainAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var data = mutableListOf<Fragment>()
    init {
        data.add(MainFragment.newInstance())
        data.add(MonthFragment.newInstance())
        data.add(UserFragment.newInstance())
    }
    override fun getItem(position: Int): Fragment {
        return data[position]
        }

    override fun getCount(): Int {
        return data.size
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}