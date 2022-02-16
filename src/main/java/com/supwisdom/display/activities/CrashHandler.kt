package com.supwisdom.display.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import com.supwisdom.commonlib.utils.*
import com.supwisdom.display.utils.AppExitUtil
import com.supwisdom.display.utils.ToastUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * @author zzq
 * @date 2018/3/20.
 * @version 1.0.1
 * @desc  崩溃日志截取，保存到SD卡
 */
class CrashHandler : Thread.UncaughtExceptionHandler {

    private var context: Context? = null
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private val info = HashMap<String, String>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CrashHandler? = null

        fun getInstance(): CrashHandler {
            if (INSTANCE == null) {
                synchronized(CrashHandler::class) {
                    if (INSTANCE == null) {
                        INSTANCE = CrashHandler()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun init(context: Context) {
        this.context = context
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        } else {
            AppExitUtil.exit()
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        CommonUtil.showBar(context!!)
        ThreadPool.getShortPool().execute {
            CommonUtil.doSleep(500)
            Looper.prepare()
            ToastUtil.show(context!!, "很抱歉,程序运行异常")
            Looper.loop()
        }
        if (ex == null) {
            return false
        }
        // 收集设备参数信息
        collectDeviceInfo()
        // 保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 收集设备参数信息
     */

    private fun collectDeviceInfo() {
        try {
            val pm = context!!.packageManager// 获得包管理器
            val pi = pm.getPackageInfo(context!!.packageName,
                    PackageManager.GET_ACTIVITIES)// 得到该应用的信息，即主Activity
            if (pi != null) {
                info["versionName"] = pi.versionName ?: "null"
                info["versionCode"] = pi.versionCode.toString()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val fields = Build::class.java.declaredFields// 反射机制
        for (field in fields) {
            try {
                field.isAccessible = true
                info[field.name] = field.get("").toString()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveCrashInfo2File(ex: Throwable): Boolean {
        val sb = StringBuffer()
        sb.append("\r\n crash time :").append(DateUtil.getNowTime()).append("\r\n")
        for ((key, value) in info) {
            sb.append("$key=$value\r\n")
        }
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        ex.printStackTrace(pw)
        var cause: Throwable? = ex.cause
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }
        pw.close()// 记得关闭
        val result = writer.toString()
        sb.append(result)
        // 保存文件
        FileUtil.writeCrashFile(sb.toString())
        return true
    }
}