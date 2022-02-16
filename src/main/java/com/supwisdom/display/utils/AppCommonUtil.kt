@file:Suppress("DEPRECATION")

package com.supwisdom.display.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager

/**
 * @author gqy
 * @date 2020/1/9 0009.
 * @desc TODO
 * @see
 * @since 1.0.0
 */
object AppCommonUtil {

    /**
     * 隐藏姓名敏感信息
     */
    fun getStuempnoSpec(custname: String?): String {
        if (custname == null) {
            return "***"
        }
        val prename = custname.substring(0, 1)
        val aftername = custname.substring(1)
        var lastname = ""
        for (i in aftername.indices) {
            lastname += "*"
        }
        return "$prename$lastname"
    }

    //关闭状态栏
    fun closeBar(context: Context) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = (context as Activity).window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = (context as Activity).window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }

    //打开状态栏
    fun showBar(context: Context) {
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = (context as Activity).window.decorView
            v.systemUiVisibility = View.VISIBLE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = (context as Activity).window.decorView
            val uiOptions = View.STATUS_BAR_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
    }

    /**
     * @param activity
     * @param brightness [1-255]
     * @desc 根据亮度值修改当前window亮度
     */
    fun setAppBrightness(activity: Activity, brightness: Int) {
        val window = activity.window
        val lp = window.attributes
        if (brightness == -1) {
            if (lp.screenBrightness < 0) {
                /**
                 * 已经是正常亮度
                 */
                return
            }
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        } else {
            if (lp.screenBrightness > 0) {
                /**
                 * 已经屏保亮度
                 */
                return
            }
            stopAutoBrightness(activity)
            lp.screenBrightness = (if (brightness <= 0) 1 else brightness) / 255f
        }
        window.attributes = lp
        /**
         * 生效？
         */
//        getActivityBrightness(activity)
    }

    /**
     * 停止自动亮度调节
     *
     * @param activity
     */
    private fun stopAutoBrightness(activity: Activity) {
        Settings.System.putInt(activity.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
    }
}
