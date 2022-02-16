package com.supwisdom.display.activities.display.mode

import android.os.Handler
import com.supwisdom.commonlib.utils.CommonUtil
import com.supwisdom.commonlib.utils.ThreadPool
import com.supwisdom.display.bean.DisplayScreenSaverBean
import com.supwisdom.display.entity.DisplayMsgRecord
import com.supwisdom.display.utils.PublicDef
import java.util.concurrent.locks.ReentrantLock

/**
 ** create by zzq on 2020/1/14
 ** @desc
 **/
class DisplayBackgroundService constructor(private val handler: Handler) {
    private val runnable = BackgroundRunnable()

    @Volatile
    private var threadFinish = false
    /**
     * 界面最后一次操作时间
     */
    @Volatile
    private var operation = false
    private var orderSortTime: Long = 0
    private val screenSaverBean = DisplayScreenSaverBean()
    private var sortEnable = true
    private val takeOrderList = ArrayList<DisplayMsgRecord>()
    @Volatile
    private var takeOrderShow = false
    private val lock = ReentrantLock()

    init {
        screenSaverBean.operationTime = System.currentTimeMillis()
        orderSortTime = System.currentTimeMillis()
    }

    fun start() {
        threadFinish = false
        ThreadPool.getSinglePool("backgroundService").execute(runnable)
    }

    fun stop() {
        threadFinish = true
        ThreadPool.getSinglePool("backgroundService").cancel(runnable)
    }

    fun resetHomeClearTime() {
        operation = true
    }

    fun setAutoSort(enable: Boolean) {
        sortEnable = enable
        if (enable) {
            orderSortTime = System.currentTimeMillis()
        }
    }

    fun resetTakeShowFlag() {
        takeOrderShow = false
    }

    fun pushTakeOrder(record: DisplayMsgRecord) {
        lock.lock()
        takeOrderList.add(record)
        lock.unlock()
    }

    private fun popTakeOrder(): DisplayMsgRecord? {
        try {
            lock.lock()
            if (takeOrderList.isNotEmpty()) {
                return takeOrderList.removeAt(0)
            }
            return null
        } finally {
            lock.unlock()
        }
    }

    private inner class BackgroundRunnable : Runnable {
        override fun run() {
            while (!threadFinish) {
                CommonUtil.doSleep(500)

                val tt = System.currentTimeMillis()
                if (operation) {
                    operation = false
                    screenSaverBean.operationTime = tt
                    screenSaverBean.operationFlag = true
                }
                if (tt > screenSaverBean.operationTime &&
                        tt - screenSaverBean.operationTime > PublicDef.SCREEN_SAVER_GAP) {
                    /**
                     * 连续1小时无操作屏保
                     */
                    screenSaverBean.operationTime = tt
                    screenSaverBean.flag = true
                    screenSaverBean.operationFlag = false
                    sendMsg(PublicDef.MSG_SCREEN_SAVE, "on")
                } else {
                    if (screenSaverBean.operationFlag && screenSaverBean.flag) {
                        screenSaverBean.flag = false
                        sendMsg(PublicDef.MSG_SCREEN_SAVE, "off")
                    }
                }
                if (!takeOrderShow) {
                    popTakeOrder()?.run {
                        takeOrderShow = true
                        sendMsg(PublicDef.MSG_TAKE_SHOW, this)
                    }
                }

                if (sortEnable) {
                    if (tt > orderSortTime && tt - orderSortTime > PublicDef.ORDER_SORT_GAP) {
                        orderSortTime = tt
                        sendMsg(PublicDef.MSG_WAIT_SORT, "")
                        sendMsg(PublicDef.MSG_TAKE_SORT, "")
                    }
                }
            }
        }

        private fun sendMsg(what: Int, any: Any) {
            handler.sendMessage(handler.obtainMessage(what, any))
        }
    }
}