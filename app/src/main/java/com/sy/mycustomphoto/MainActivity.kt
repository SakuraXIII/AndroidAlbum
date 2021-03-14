package com.sy.mycustomphoto

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sy.mycustomphoto.ui.MyFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {
    private val permissionList: Array<String> = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var tabList = listOf<String>()
    private var mSharedPreferences: SharedPreferences? = null
    private var fragmentList = arrayListOf<Fragment>()
    private var num: Int = 0 //计数，返回键二次确认才退出


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Android 6.0 以后，敏感权限需要动态获取权限
        if (checkPermission()) init()
    }

    /**
     * @Description: 检查权限后进行Activity初始化，读取SharedPerferences中的配重并加载一个Fragment数据
     * @Param: null
     * @return: void
     * @Author: SY
     * @Date: 21/03/07 22:06
     */
    private fun init() {
        mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE)
        val tab = mSharedPreferences?.getString("tabList", null)
        tabList = if (tab.isNullOrBlank()) readDirName().toList() else tab.split(",")
        for (i in tabList) {
            tab_list.addTab(tab_list.newTab().setText(i))
            val showImgFragment = ShowImgFragment(i)
            fragmentList.add(showImgFragment)
        }
        createViewPager()
        setTabListenerEvent()
    }

    private fun setTabListenerEvent() {
        tab_list.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    view_pager.setCurrentItem(tab.position, true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }


    /**
     * @Description: 获取手机DCIM目录下所有相册目录名
     * @Param: null
     * @return:
     * @Author: SY
     * @Date: 21/03/08 13:43
     */
    private fun readDirName(): Array<String> {
        var nameList = arrayOf<String>()
        try {
            val files =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            nameList = files.list { _, s ->
                return@list s.indexOf(".") < 0 && !s.equals("mitime")
            } ?: arrayOf()

        } catch (e: FileNotFoundException) {
            Log.d("TAG", e.toString())
            e.printStackTrace()
        }
        return nameList
    }


    private fun createViewPager() {
        val myFragmentPagerAdapter = MyFragmentPagerAdapter(supportFragmentManager, fragmentList)
        view_pager.adapter = myFragmentPagerAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tab_list.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    /**
     * @Description: 检查权限
     * @Param: null
     * @return: void
     * @Author: SY
     * @Date: 21/03/07 22:05
     */
    private fun checkPermission(): Boolean {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取 Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        // 检查该权限是否已经获取
        for (permission in permissionList) {
            val isGranted = ContextCompat.checkSelfPermission(applicationContext, permission);
            if (isGranted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissionList, 1)
                return false
            }
        }
        return true
    }

    /**
     * @Description: 请求权限回调
     * @Param: null
     * @return: void
     * @Author: SY
     * @Date: 21/03/07 22:05
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissionList.indices) {
                if (grantResults.isNotEmpty() && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "未授权${permissions[i]}的权限", Toast.LENGTH_LONG).show()
                }
            }
            init()
        }
    }

    override fun onStop() {
        super.onStop()
        mSharedPreferences?.edit()?.putString("tabList", tabList.joinToString(","))?.apply()
    }

    override fun onBackPressed() {
        if (num == 1) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "二次确认再退出", Toast.LENGTH_SHORT).show()
            num++
        }
    }


}