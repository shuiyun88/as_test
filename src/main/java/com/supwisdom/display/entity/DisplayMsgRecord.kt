package com.supwisdom.display.entity

/**
 * @author zzq
 * @date 2018/4/4.
 * @version 1.0.1
 * @desc  配置参数
 */
class DisplayMsgRecord : Comparable<DisplayMsgRecord> {
    var mealid: Int = 0
    var windowid: String? = null
    var custname: String? = null
    var windowname: String? = null
    var billno: String? = null
    var orderno: String? = null
    var transdate: String? = null
    var transtime: String? = null
    var takemealstatus: String? = null  // 0-未取餐(待备餐) 1-已取餐 2-已叫号  3-备餐完成,等待叫号  4--冲正
    override fun compareTo(other: DisplayMsgRecord): Int {
        return this.orderno!!.compareTo(other.orderno!!)
    }
}