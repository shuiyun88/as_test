package com.supwisdom.display.utils

/**
 * Created by gqy on 2019/1/3.
 */

object PublicDef {
    /**
     * socket消息类型
     */
    val ACTION_SHOWNUM = "noshow_num" //刷新排队人数
    val ACTION_WAIT = "0" //备餐中
    val ACTION_TAKEMEAL = "1" //已取餐
    val ACTION_CALL = "2" //叫号
    val ACTION_WAITCALL = "3" //已备餐，等待叫号
    val ACTION_REVERSE = "4" //冲正
    val ACTION_AUTH = "login" //签到

    /**
     * 屏保
     */
    const val SCREEN_SAVER_GAP: Long = 60 * 60 * 1000
    const val SCREEN_SAVER_BRIGHTNESS = 1
    const val SCREEN_NORMAL_BRIGHTNESS = -1

    const val ORDER_SORT_GAP: Long = 5 * 1000


    val MSG_AUTH_FAIL = 0
    val MSG_AUTH_SUC = 1
    val MSG_WAIT = 2
    val MSG_CALL = 3
    val MSG_TAKEMEAL = 4
    val MSG_SOCKET_PROCESS = 5

    val MSG_CIRCLE_SHOW = 6
    val MSG_SCREEN_SAVE = 7
    val MSG_WAIT_SORT = 8
    val MSG_TAKE_SORT = 9
    val MSG_TAKE_SHOW = 10
    val MSG_REVERSE= 11
}
