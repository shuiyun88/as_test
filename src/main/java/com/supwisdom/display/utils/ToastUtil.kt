package com.supwisdom.display.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.supwisdom.display.R

/**
 * @author zzq
 * @date 2018/3/20.
 * @version 1.0.1
 * @desc  界面操作提示
 */
@SuppressLint("StaticFieldLeak")
object ToastUtil {
    fun show(context: Context, msg: String?) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.util_toast, null)
        val toastText = view.findViewById(R.id.toasttext) as TextView
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toastText.text = msg
        toast.show()
    }

    fun show(context: Context, msg: String?, listener: ShowListener) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.util_toast, null)

        val toastText = view.findViewById(R.id.toasttext) as TextView
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = view
        toastText.text = msg
        toast.show()
        val counter = object : CountDownTimer(2000, 200) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                listener.callback()
                toast.cancel()
            }
        }
        counter.start()
    }

    interface ShowListener {
        fun callback()
    }
}