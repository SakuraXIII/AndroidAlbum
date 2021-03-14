package com.sy.mycustomphoto

import android.app.Application

/**
 * @program: MyCustomPhoto
 * @description: 自定义Applicatin
 * @author: SY
 * @create: 2021-03-09 12:20
 **/
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val catchHandler: CrashHandle = CrashHandle.getInstance()
        catchHandler.init(applicationContext)
    }
}