package com.supwisdom.display.service

import android.content.Context
import android.os.Handler
import android.util.Log
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import com.supwisdom.commonlib.utils.GsonUtil
import com.supwisdom.commonlib.utils.LogUtil
import com.supwisdom.display.bean.MsgAction
import com.supwisdom.display.bean.MsgData
import com.supwisdom.display.entity.DisplayMsgRecord
import com.supwisdom.display.utils.PublicDef
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Created by gqy on 2019/1/3.
 */
class WebSocketService private constructor() {
    private var wsManager: WsManager? = null
    private lateinit var handler: Handler
    private val TAG = "WebSocketService"
    private var localUrl: String? = null
    private var localPhyid: String? = null

    fun checkIfReconnect(url: String, devphyid: String): Boolean {
        if (localUrl == null || localUrl != url) {
            return true
        }
        if (localPhyid == null || localPhyid != devphyid) {
            return true
        }
        return false
    }

    fun start(context: Context, url: String, devphyid: String, handler: Handler) {
        if (!checkIfReconnect(url, devphyid)) {
            return
        }
        close()
        this.handler = handler
        this.localUrl = url
        this.localPhyid = devphyid

        val okHttpClient = OkHttpClient().newBuilder()
                .pingInterval(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        LogUtil.d(TAG, "socket url:$url,devphyid:$devphyid")
        wsManager = WsManager.Builder(context)
                .wsUrl("ws://" + url + "/services/websocket/devlogin?deviceid=" + devphyid + "_noshow")
                .needReconnect(true)
                .client(okHttpClient)
                .build()
        try {
            wsManager!!.startConnect()
        } catch (ex: java.lang.Exception) {
            sendMsg("url is invalid,url=$url", PublicDef.MSG_SOCKET_PROCESS)
            return
        }
        wsManager!!.setWsStatusListener(object : WsStatusListener() {
            override fun onOpen(response: Response) {
                super.onOpen(response)
//                sendMsg("onOpen", PublicDef.MSG_SOCKET_PROCESS)
            }

            override fun onMessage(text: String?) {
                super.onMessage(text)
                LogUtil.d(TAG, text)
                if (text != null) {
                    parse(text)
                }
            }

            override fun onReconnect() {
                super.onReconnect()
                sendMsg("onReconnect,url=$url", PublicDef.MSG_SOCKET_PROCESS)
            }

            override fun onClosing(code: Int, reason: String?) {
                super.onClosing(code, reason)
                LogUtil.e(TAG, "onClosing,code:$code,reason:$reason")
                sendMsg("onClosing,$reason", PublicDef.MSG_SOCKET_PROCESS)
            }

            override fun onClosed(code: Int, reason: String?) {
                super.onClosed(code, reason)
                LogUtil.e(TAG, "onClosed,$code,$reason")
                sendMsg("onClosed,reason:$reason", PublicDef.MSG_SOCKET_PROCESS)
            }

            override fun onFailure(t: Throwable?, response: Response?) {
                super.onFailure(t, response)
                LogUtil.e(TAG, "onFailure," + "," + response?.code + "," + t?.message + "," + response?.message)
                sendMsg("onFailure,code=" + response?.code + ",msg=" + response?.message + "url=$url", PublicDef.MSG_SOCKET_PROCESS)
            }
        })
    }

    private fun sendMsg(msg: Any, what: Int) {
        handler.sendMessage(handler.obtainMessage(what, msg))
    }

    private fun parse(msg: String) {
        try {
            val msgAction = GsonUtil.GsonToBean(msg, MsgAction::class.java)
            if (msgAction != null) {
                when (msgAction.action) {
                    PublicDef.ACTION_WAIT, PublicDef.ACTION_WAITCALL -> {
                        msgAction.data?.run {
                            val record = getDisplayMsg(this)
                            record.takemealstatus = msgAction.action
                            sendMsg(record, PublicDef.MSG_WAIT)
                        }
                    }
                    PublicDef.ACTION_CALL -> {
                        msgAction.data?.run {
                            val record = getDisplayMsg(this)
                            record.takemealstatus = "2"
                            sendMsg(record, PublicDef.MSG_CALL)
                        }
                    }
                    PublicDef.ACTION_TAKEMEAL, PublicDef.ACTION_REVERSE -> {
                        msgAction.data?.run {
                            val record = getDisplayMsg(this)
                            record.takemealstatus = msgAction.action
                            sendMsg(record, PublicDef.MSG_TAKEMEAL)
                        }
                    }
                    PublicDef.ACTION_AUTH -> {
                        sendMsg("签到成功", PublicDef.MSG_AUTH_SUC)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "消息解析失败:${e.message}")
        }
    }

    private fun getDisplayMsg(data: MsgData): DisplayMsgRecord {
        val record = DisplayMsgRecord()
        record.billno = data.billno
        record.mealid = data.mealid
        record.custname = data.custname
        record.windowid = data.windowid
        record.windowname = data.windowname
        record.orderno = data.orderno
        record.transdate = data.orderdate
        record.transtime = data.ordertime
        return record
    }

    fun close() {
        wsManager?.stopConnect()
        wsManager = null
        this.localUrl = null
        this.localPhyid = null
    }

    companion object {
        var mInstance: WebSocketService? = null

        fun getInstance(): WebSocketService {
            if (mInstance == null) {
                mInstance = WebSocketService()
            }
            return mInstance!!
        }
    }
}
