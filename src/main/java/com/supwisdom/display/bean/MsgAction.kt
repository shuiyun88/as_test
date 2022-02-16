package com.supwisdom.display.bean

/**
 * Created by shuwei on 2019/1/3.
 */

class MsgAction {
    var action: String? = null
    var data: MsgData? = null
}

class MsgData {
    var billno: String? = null
    var orderno: String? = null // 取餐号
    var custname: String? = null
    var windowid: String? = null
    var mealid: Int = 0
    var mealname: String? = null
    var windowname: String? = null
    var goods: String? = null
    var num: Int = 0
    var orderdate: String? = null
    var ordertime: String? = null
    var systime: String? = null
}
