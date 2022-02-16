package com.supwisdom.display.activities.display

import android.app.Activity
import com.supwisdom.display.entity.DisplayMsgRecord

/**
 * @author gqy
 * @date 2020/1/7
 * @since 1.0.0
 * @see
 * @desc  大屏主页
 */
interface IDisplayView {
    fun getActivity(): Activity

    fun refreshOrderMsg(wait: List<DisplayMsgRecord>, take: List<DisplayMsgRecord>)

    fun screenSaver(flag: Boolean)

    fun showNewWaitDisplay(record: DisplayMsgRecord)

    fun showNewTakeDisplay(record: DisplayMsgRecord)

    fun disTakeDisplay(record: DisplayMsgRecord)

    fun showConnectSuccess()

    fun showConnectFail(msg: String)

    fun refreshWaitSort()

    fun refreshTakeSort()
}