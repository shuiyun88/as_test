package com.supwisdom.display

import android.annotation.SuppressLint
import android.content.Context
import com.supwisdom.display.db.DisplayPos

/**
 * @author zzq
 * @date 2018/4/4.
 * @version 1.0.1
 * @desc  数据库句柄
 */
@SuppressLint("StaticFieldLeak")
internal object Pos {

    var context: Context? = null
    @SuppressLint("StaticFieldLeak")
    private var INSTANCE: DisplayPos? = null

    fun getInstance(): DisplayPos {
        if (INSTANCE == null) {
            synchronized(Pos::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = DisplayPos(context!!)
                }
            }
        }
        return INSTANCE!!
    }
}

