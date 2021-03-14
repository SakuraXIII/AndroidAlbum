package com.sy.mycustomphoto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import com.sy.mycustomphoto.ui.MyPictureInfo
import java.io.File

class MyGetBitmapFromFile(private val handler: Handler, imgDir: String) {

    private var files: Array<File>? = null
    private var index: Int = 0

    private val runnable = Runnable {
        val temp: Int = index + 30
        val endPosition: Int =
            if (temp > files!!.size) files!!.size else temp
        for (i in index until endPosition) {
            val path = files!![i].path
            // 判断是否为图片  系统内置相册目录中可能有配置文件等非图片资源
            if (!files!![i].extension.matches(Regex(("jpg|jpeg|png|webp|gif|bmp")))) continue
            val bm = compreImg(path)
            val message = Message()
            message.what = 1
            message.obj = MyPictureInfo(i, path, bm)
            handler.sendMessage(message)
        }
        index = temp
    }


    init {
        files = File(imgDir).listFiles()
    }

    fun getNextImg() {
        if (files.isNullOrEmpty()) {
            val message = Message()
            message.what = 2
            message.obj = "没有找到图片，是不是没有读写权限呢？"
            handler.sendMessage(message)
            return
        }
        Thread(runnable).start()
    }


    private fun compreImg(path: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(path, options)
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 3 // 默认像素压缩比例，压缩为原图的1/2
        val minLen = height.coerceAtMost(width) // 原图的最小边长
        if (minLen > 500) { // 如果原始图像的最小边长大于200px
            val ratio = minLen.toFloat() / 300.0f // 计算像素压缩比例 ?:1
            inSampleSize = ratio.toInt()
        }
        options.inJustDecodeBounds = false // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize // 设置为刚才计算的压缩比例
        return BitmapFactory.decodeFile(path, options)
    }
}