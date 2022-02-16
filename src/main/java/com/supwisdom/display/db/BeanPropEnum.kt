package com.supwisdom.display.db

/**
 * @author zzq
 * @date 2018/3/30.
 * @version 1.0.1
 * @desc  数据库存储元素
 */
internal class BeanPropEnum {

    enum class OrderConfig {
        id,
        devphyid,
        ip,
        port,
        initOK
    }

    enum class OrderMsg {
        billno,
        mealid,
        windowid,
        windowname,
        custname,
        orderno,
        transdate,
        transtime,
        takemealstatus
    }
}