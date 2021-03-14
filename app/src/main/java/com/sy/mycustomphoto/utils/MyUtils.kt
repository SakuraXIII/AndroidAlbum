package com.sy.mycustomphoto.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * @program: MyCustomPhoto
 * @description: 自定义工具类
 * @author: SY
 * @create: 2021-03-08 20:42
 **/
class MyUtils {
    companion object {
        fun log(str: String) {
            Log.d("TAG", str)
        }

        fun toastShort(context: Context, str: String) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }

        fun toastLong(context: Context, str: String) {
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }

    }
}