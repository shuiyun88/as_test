package com.supwisdom.display.activities.init

import android.app.Activity
/**
 * @author gqy
 * @date 2018/5/24
 * @version 1.0.1
 * @desc  初始化
 */
interface InitView {
    fun getActivity(): Activity

    fun showToast(msg: String)

    fun saveConfigSuc()
}