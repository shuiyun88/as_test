package com.supwisdom.display.activities.display

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.supwisdom.commonlib.utils.DateUtil
import com.supwisdom.commonlib.utils.FileUtil
import com.supwisdom.commonlib.utils.ThreadPool
import com.supwisdom.display.Pos
import com.supwisdom.display.activities.display.mode.DisplayBackgroundService
import com.supwisdom.display.entity.DisplayMsgRecord
import com.supwisdom.display.service.WebSocketService
import com.supwisdom.display.utils.PublicDef

/**
 * @author gqy
 * @date 2020/1/3
 * @since 1.0.0
 * 大屏主页
 */
class DisplayPresenter(private val iDisplayView: IDisplayView) {
    private val TAG = "DisplayPresenter"
    private val pos = Pos.getInstance()
    private val backgroundService: DisplayBackgroundService
    private val socketService = WebSocketService.getInstance()
    private lateinit var handler: Handler
    private var lastMealId: Int = 0
    private var lastMealDate: String? = null
    private var lastTakeBillno: String? = null //最后一笔取餐流水
    private var lastTakeBillnoTime: Long = 0 //最后一笔取餐时间
    private val DEBUG = false

    init {
        createHandler()
        backgroundService = DisplayBackgroundService(handler)
    }

    private fun createHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    PublicDef.MSG_CIRCLE_SHOW -> {
                    }
                    PublicDef.MSG_SCREEN_SAVE -> {
                        iDisplayView.screenSaver(msg.obj as String == "on")
                    }
                    PublicDef.MSG_WAIT_SORT -> {
                        iDisplayView.refreshWaitSort()
                    }
                    PublicDef.MSG_TAKE_SORT -> {
                        iDisplayView.refreshTakeSort()
                    }
                    PublicDef.MSG_WAIT -> {
                        resetHomeClearTime()
                        val record = msg.obj as DisplayMsgRecord
                        if (checkAndClearOrderMsg(record)) {
                            loadOrderMsgList()
                        } else {
                            iDisplayView.showNewWaitDisplay(record)
                        }
                    }
                    PublicDef.MSG_CALL -> {
                        resetHomeClearTime()
                        val record = msg.obj as DisplayMsgRecord

                        /**
                         * 防止重复连续推送
                         */
                        val tt = System.currentTimeMillis()
                        if (lastTakeBillno == record.billno) {
                            if (tt - lastTakeBillnoTime < 3000) {
                                return
                            }
                        }
                        lastTakeBillno = record.billno
                        lastTakeBillnoTime = tt

                        if (checkAndClearOrderMsg(record)) {
                            loadOrderMsgList()
                        } else {
                            backgroundService.pushTakeOrder(record)
                        }
                    }
                    PublicDef.MSG_TAKE_SHOW -> {
                        iDisplayView.showNewTakeDisplay(msg.obj as DisplayMsgRecord)
                    }
                    PublicDef.MSG_TAKEMEAL -> {
                        resetHomeClearTime()
                        val record = msg.obj as DisplayMsgRecord
                        pos.replaceMsgRecord(record)
                        iDisplayView.disTakeDisplay(record)
                    }
                    PublicDef.MSG_AUTH_SUC -> {
                        iDisplayView.showConnectSuccess()
                    }
                    PublicDef.MSG_AUTH_FAIL,
                    PublicDef.MSG_SOCKET_PROCESS -> {
                        iDisplayView.showConnectFail(msg.obj as String)
                    }
                }
            }
        }
    }

    /**
     * @desc 一段时间后界面无操作,刷新清空界面
     */
    fun resetHomeClearTime() {
        backgroundService.resetHomeClearTime()
    }

    fun loadOrderMsgList() {
        ThreadPool.getShortPool().execute {
            removeOldLog()
            val wait = ArrayList<DisplayMsgRecord>()
            val take = ArrayList<DisplayMsgRecord>()
            val list = pos.getAllMsgRecord()
            if (list.isNotEmpty()) {
                lastMealDate = list[0].transdate
                lastMealId = list[0].mealid
                list.forEach {
                    when (it.takemealstatus) {
                        PublicDef.ACTION_WAIT, PublicDef.ACTION_WAITCALL -> {
                            wait.add(it)
                        }
                        PublicDef.ACTION_CALL -> {
                            take.add(it)
                        }
                    }
                }
            }
            iDisplayView.getActivity().runOnUiThread {
                if (DEBUG) {
                    iDisplayView.refreshOrderMsg(getTestWaitDisplay(), getTestTakeDisplay())
                } else {
                    iDisplayView.refreshOrderMsg(wait, take)
                }
            }
        }
    }

    fun start() {
        backgroundService.start()
    }

    fun stop() {
        backgroundService.stop()
    }

    fun setAutoSort(enable: Boolean) {
        backgroundService.setAutoSort(enable)
    }

    fun resetTakeShowFlag() {
        backgroundService.resetTakeShowFlag()
    }

    private fun checkAndClearOrderMsg(record: DisplayMsgRecord): Boolean {
        var flag = false
        if ((lastMealId != record.mealid && lastMealId != 0) ||
                (record.transdate != null && record.transdate != lastMealDate)) {
            pos.clearMsgRecord()
            flag = true
        }
        pos.replaceMsgRecord(record)
        return flag
    }

    fun refreshSocketConnect() {
        val cfgRecord = pos.getConfigPara()
        val url = "${cfgRecord!!.ip}:${cfgRecord.port}/posapi"
        if (socketService.checkIfReconnect(url, cfgRecord.devphyid!!)) {
            socketService.start(iDisplayView.getActivity(), url, cfgRecord.devphyid!!, handler)
        }
    }

    fun stopSocketConnect() {
        socketService.close()
    }

    private fun removeOldLog() {
        FileUtil.deleteLogFile(FileUtil.getLogFile(), 5)
        FileUtil.removeCrashFile(5)
    }

    private fun getTestWaitDisplay(): List<DisplayMsgRecord> {
        val list = ArrayList<DisplayMsgRecord>()
        var i = 0
        while (i++ < 14) {
            val record = DisplayMsgRecord()
            record.billno = "${DateUtil.getNowDateTimeNoFormat()}$i"
            record.orderno = String.format("1%03d", i + 1)
            record.takemealstatus = "0"
            record.mealid = 10
            record.windowname = "测试窗口"
            record.custname = "张宗强"
            list.add(record)
        }
        return list
    }

    private fun getTestTakeDisplay(): List<DisplayMsgRecord> {
        val list = ArrayList<DisplayMsgRecord>()
        var i = 0
        while (i++ < 5) {
            val record = DisplayMsgRecord()
            record.billno = "${DateUtil.getNowDateTimeNoFormat()}$i"
            record.orderno = String.format("1%03d", i + 1)
            record.takemealstatus = "2"
            record.mealid = 10
            record.windowname = "测试窗口"
            record.custname = "张宗强"
            list.add(record)
        }
        return list
    }

    private fun getNewTakeDisplay(): DisplayMsgRecord {
        val record = DisplayMsgRecord()
        val i = (System.currentTimeMillis() % 1000).toInt()
        record.billno = "${DateUtil.getNowDateTimeNoFormat()}$i"
        record.orderno = String.format("1%03d", i)
        record.takemealstatus = "2"
        record.mealid = 10
        record.windowname = "测试窗口"
        record.custname = "张宗强"
        return record
    }

    fun testNewTakeDisplay() {
        if (DEBUG) {
            backgroundService.pushTakeOrder(getNewTakeDisplay())
        }
    }
}