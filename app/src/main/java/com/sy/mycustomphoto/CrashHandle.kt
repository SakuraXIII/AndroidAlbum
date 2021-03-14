package com.sy.mycustomphoto

import android.content.Context
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Toast
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess


/**
 * @program: MyCustomPhoto
 * @description: 异常捕获
 * @author: SY
 * @create: 2021-03-09 12:21
 **/
class CrashHandle : Thread.UncaughtExceptionHandler {


    val TAG = "CrashHandler"

    //系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null


    //程序的Context对象
    private var mContext: Context? = null

    //用来存储设备信息和异常信息
    private val infos: Map<String, String> = HashMap()

    //用于格式化日期,作为日志文件名的一部分
    private val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        if (!handleException(p1) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(p0, p1)
        } else {
            Log.e("TAG", "程序崩溃了，请查看控制台输出栈信息");
            try {
                Thread.sleep(3000);
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : ", e);
            }
        }

        Process.killProcess(Process.myPid())
        exitProcess(1)
    }


    fun init(context: Context) {
        mContext = context
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }

        //使用Toast来显示异常信息
        object : Thread() {
            override fun run() {
                Looper.prepare()
                Toast.makeText(mContext, "很抱歉,程序出现异常,请退出.", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }.start()
        //保存日志文件
        ex.printStackTrace()
        saveCatchInfo2File(ex)
        return true
    }

    private fun saveCatchInfo2File(ex: Throwable): String? {
        val sb = StringBuffer()
        for ((key, value) in infos) {
            sb.append("$key=$value\n")
        }
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result: String = writer.toString()
        sb.append(result)
        try {
            val timestamp = System.currentTimeMillis()
            val time = formatter.format(Date())
            val fileName = "crash-$time-$timestamp.log"
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                val file =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                Log.d("TAG", file.name + File.separator + fileName)
                val fos = FileOutputStream(file.path + File.separator + fileName)
                fos.write(sb.toString().toByteArray())
                fos.close()
            }

            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing file...", e)
        }
        return null
    }


    companion object {
        private var instance: CrashHandle? = null
        fun getInstance(): CrashHandle {
            if (instance == null) {
                instance = CrashHandle()
            }
            return instance!!
        }
    }
}