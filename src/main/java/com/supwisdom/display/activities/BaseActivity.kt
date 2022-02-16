package com.supwisdom.display.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.supwisdom.display.utils.AppExitUtil

/**
 * @desc 基类activity
 */
open class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppExitUtil.add(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onDestroy() {
        AppExitUtil.remove(this)
        super.onDestroy()
    }

    fun <T> jumpToActivity(cls: Class<T>) {
        startActivity(Intent().setClass(this, cls))
    }
}
