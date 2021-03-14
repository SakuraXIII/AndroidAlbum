package com.sy.mycustomphoto

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sy.mycustomphoto.ui.MyPictureInfo
import com.sy.mycustomphoto.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_show_img.*
import java.io.File

class ShowImgFragment(private val photoPath: String = "Camera") : Fragment() {

    private var processBarIsHide: Boolean = false
    private val handler: Handler = Handler() {
        when (it.what) {
            1 -> {
                myRecView.addItem(it.obj as MyPictureInfo)
            }
            2 -> {
                not_img.text = it.obj as String
                not_img.visibility = View.VISIBLE
            }
        }
        if (!processBarIsHide) {
            processBarIsHide = true
            load_bar.visibility = View.GONE
        }
        return@Handler true
    }
    private lateinit var myRecView: MyRecyclerViewInfo
    private var mTotal = 0
    private var mFragmentView: View? = null
    private var flag = true
    private val mGetBitmapFile: MyGetBitmapFromFile = MyGetBitmapFromFile(
        handler,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + File.separator + this.photoPath
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_show_img, container, false)
        }
        return mFragmentView
    }


    override fun onResume() {
        super.onResume()
        if (flag) {
            context?.let {
                myRecView = MyRecyclerViewInfo(it, recycler_view, 3)
                setImgBtnAnim()
            }
            startReadPhotoDir()
            flag = false
        }
    }


    /**
     * @Description: 设置回到顶部按钮的动画
     * @Param: null
     * @return: void
     * @Author: SY
     * @Date: 21/03/07 23:42
     */
    private fun setImgBtnAnim() {
        clickToTop.visibility = View.GONE
        clickToTop.setOnClickListener {
            myRecView.getView().smoothScrollToPosition(0)
        }
        setRecViewEvent()
    }

    /**
     * @Description: 开始读取指定路径下的图片
     * @Param: null
     * @return: void
     * @Author: SY
     * @Date: 21/03/07 23:41
     */
    private fun startReadPhotoDir() {
        mGetBitmapFile.getNextImg()
    }

    /**
     * @Description: RecyclerView 滑动监听事件
     * @return: void
     * @Author: SY
     * @Date: 21/03/06 14:49
     */
    private fun setRecViewEvent() {
        val hideAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.btn_hide_anim)
        val showAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.btn_show_anim)
        myRecView.addMyRecViewEvent(object : MyRecyclerViewInfo.MyRecViewEvent {
            var flag = true
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mTotal += dy
                if (mTotal > 500 && flag) {
                    clickToTop.run {
                        visibility = View.VISIBLE
                        startAnimation(showAnim)
                        flag = false
                    }
                } else if (mTotal in 0..500 && !flag) {
                    clickToTop.run {
                        visibility = View.GONE
                        startAnimation(hideAnim)
                        flag = true
                    }
                }
            }

            override fun onItemClick(position: Int, path: String) {
                val intent = Intent(context, ShowActivity::class.java)
                intent.putExtra("path", path)
                intent.putExtra("position", position)
                startActivity(intent)
            }

            override fun onItemLongClick(position: Int, path: String) {
                super.onItemLongClick(position, path)
                Toast.makeText(context, "这是第$position 个item", Toast.LENGTH_LONG).show()
            }

            override fun requestLoad(totalItemCount: Int) {
                MyUtils.log("========")
                startReadPhotoDir()
            }
        })
    }

}