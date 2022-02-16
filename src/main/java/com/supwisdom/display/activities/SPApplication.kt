package com.supwisdom.display.activities

import android.app.Application
import com.supwisdom.display.Pos
/**
 * @author gqy
 * @date 2019/1/3.
 * @version 1.0.1
 * @desc : 应用扩展
 */
class SPApplication : Application() {
    companion object {
        @Volatile
        private var mInstance: SPApplication? = null

        fun getInstance(): SPApplication {
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Pos.context = this
        CrashHandler.getInstance().init(applicationContext)
    }
}