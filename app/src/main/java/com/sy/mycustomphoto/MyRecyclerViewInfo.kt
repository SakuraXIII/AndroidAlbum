package com.sy.mycustomphoto

import android.content.Context
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sy.mycustomphoto.ui.MyPictureInfo
import com.sy.mycustomphoto.ui.StaggeredGridLayoutAdapter
import com.sy.mycustomphoto.utils.MyUtils
import java.util.*

class MyRecyclerViewInfo(context: Context, rv: RecyclerView, col: Int = 3) {
    private var adapter: StaggeredGridLayoutAdapter
    private val lm = StaggeredGridLayoutManager(col, StaggeredGridLayoutManager.VERTICAL)
    private val recyclerView: RecyclerView = rv
    private lateinit var mRecViewEvent: MyRecViewEvent

    init {
        adapter = StaggeredGridLayoutAdapter(context,
            object : StaggeredGridLayoutAdapter.OnclickItemListener {
                override fun onClick(position: Int, path: String) {
                    mRecViewEvent.onItemClick(position, path)
                }

                override fun onLongClick(position: Int, path: String) {
                    mRecViewEvent.onItemLongClick(position, path)
                }
            })
        getView().let {
            it.layoutManager = lm
            it.itemAnimator = DefaultItemAnimator()
            it.adapter = adapter
        }

        addOnScrollListener()
    }

    fun getView(): RecyclerView {
        return recyclerView
    }

    fun addItem(img: MyPictureInfo) {
        adapter.addItem(img)
    }

    fun getPath(current: Int, step: Int): String {
        return adapter.getPath(current, step)
    }

    private fun addOnScrollListener() {
        getView().addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mRecViewEvent.onScrolled(recyclerView, dx, dy)
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                //spanCount 当前布局一行显示的View个数 这里瀑布流布局为3
                val intArray = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(intArray)
                //获取当前累计显示在屏幕中的最后一个item的计数值
                val lastVisiableItemCount: Int = Arrays.stream(intArray).max().asInt
                //View总个数
                val totalItemCount: Int = layoutManager.itemCount
                //当前屏幕显示的View的个数
                val visibleItemCount: Int = layoutManager.childCount
                MyUtils.log(visibleItemCount.toString())
                if (visibleItemCount > 0 &&
                    lastVisiableItemCount >= totalItemCount - 1 &&
                    newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    //请求继续加载
                    mRecViewEvent.requestLoad(totalItemCount)
                }
            }
        })
    }


    fun addMyRecViewEvent(mEvent: MyRecViewEvent) {
        mRecViewEvent = mEvent
    }

    interface MyRecViewEvent {
        fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        fun onItemClick(position: Int, path: String) {}
        fun onItemLongClick(position: Int, path: String) {}
        fun requestLoad(totalItemCount:Int) {}
    }
}