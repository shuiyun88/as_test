package com.supwisdom.display.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.supwisdom.display.entity.DisplayMsgRecord
import java.util.concurrent.locks.Lock

/**
 * @author zzq
 * @date 2018/3/30.
 * @version 1.0.1
 * @desc  消息
 */
internal class DisplayMsgDao constructor(context: Context) {
    private val dbHelper = DisplayMsgDBHelper.getInstance(context)
    private val TABLE = DisplayMsgDBHelper.TABLE_NAME_ORDERMSG

    fun getLock(): Lock {
        return dbHelper.getLock()
    }

    fun replace(record: DisplayMsgRecord): Boolean {
        val db = dbHelper.writableDatabase
        try {
            db.beginTransaction()
            val values = getContentValues(record)
            if (db.replace(TABLE, null, values) > 0) {
                db.setTransactionSuccessful()
                return true
            }
        } finally {
            db.endTransaction()
        }
        return false
    }

    /**
     * 查询所有未取餐订单
     */
    fun getAll(): List<DisplayMsgRecord> {
        val list = arrayListOf<DisplayMsgRecord>()
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null
        try {
            val selction = "${BeanPropEnum.OrderMsg.takemealstatus}<>? and " +
                    "${BeanPropEnum.OrderMsg.takemealstatus}<>?"
            val selctionArgs = arrayOf("1", "4")
            val orderby = "${BeanPropEnum.OrderMsg.transdate} desc"
            cursor = db.query(TABLE, null, selction, selctionArgs, null, null, orderby)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    list.add(getRecord(cursor))
                }
            }
        } finally {
            cursor?.close()
        }
        return list
    }

    fun clear(): Boolean {
        val db = dbHelper.writableDatabase
        try {
            db.beginTransaction()
            if (db.delete(TABLE, null, null) >= 0) {
                db.setTransactionSuccessful()
                return true
            }
        } finally {
            db.endTransaction()
        }
        return false
    }

    private fun getContentValues(record: DisplayMsgRecord): ContentValues {
        val values = ContentValues()
        values.put(BeanPropEnum.OrderMsg.billno.toString(), record.billno)
        values.put(BeanPropEnum.OrderMsg.mealid.toString(), record.mealid)
        values.put(BeanPropEnum.OrderMsg.windowid.toString(), record.windowid)
        values.put(BeanPropEnum.OrderMsg.custname.toString(), record.custname)
        values.put(BeanPropEnum.OrderMsg.windowname.toString(), record.windowname)
        values.put(BeanPropEnum.OrderMsg.orderno.toString(), record.orderno)
        values.put(BeanPropEnum.OrderMsg.transdate.toString(), record.transdate)
        values.put(BeanPropEnum.OrderMsg.transtime.toString(), record.transtime)
        values.put(BeanPropEnum.OrderMsg.takemealstatus.toString(), record.takemealstatus)
        return values
    }

    private fun getRecord(cursor: Cursor): DisplayMsgRecord {
        val record = DisplayMsgRecord()
        record.billno = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.billno.toString()))
        record.mealid = cursor.getInt(cursor.getColumnIndex(BeanPropEnum.OrderMsg.mealid.toString()))
        record.windowid = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.windowid.toString()))
        record.custname = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.custname.toString()))
        record.windowname = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.windowname.toString()))
        record.orderno = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.orderno.toString()))
        record.transdate = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.transdate.toString()))
        record.transtime = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.transtime.toString()))
        record.takemealstatus = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderMsg.takemealstatus.toString()))
        return record
    }
}