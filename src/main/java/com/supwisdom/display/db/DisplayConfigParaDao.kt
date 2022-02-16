package com.supwisdom.display.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.supwisdom.display.entity.DisplayConfigParaRecord
import java.util.concurrent.locks.Lock

/**
 * @author zzq
 * @date 2018/3/30.
 * @version 1.0.1
 * @desc  通讯配置参数
 */
internal class DisplayConfigParaDao constructor(context: Context) {
    private val INDEX = 1
    private val dbHelper = DisplayConfigDBHelper.getInstance(context)
    private val TABLE = DisplayConfigDBHelper.TABLE_NAME_CONFIG

    fun getLock(): Lock {
        return dbHelper.getLock()
    }

    fun replace(record: DisplayConfigParaRecord): Boolean {
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

    fun get(): DisplayConfigParaRecord? {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null
        val selection = BeanPropEnum.OrderConfig.id.toString() + "=?"
        val selectionArgs = arrayOf(INDEX.toString())
        try {
            cursor = db.query(TABLE, null, selection, selectionArgs, null, null, null)
            if (cursor != null && cursor.moveToNext()) {
                return getRecord(cursor)
            }
        } finally {
            cursor?.close()
        }
        return null
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

    private fun getContentValues(record: DisplayConfigParaRecord): ContentValues {
        val values = ContentValues()
        values.put(BeanPropEnum.OrderConfig.id.toString(), INDEX)
        values.put(BeanPropEnum.OrderConfig.devphyid.toString(), record.devphyid)
        values.put(BeanPropEnum.OrderConfig.ip.toString(), record.ip)
        values.put(BeanPropEnum.OrderConfig.port.toString(), record.port)
        if (record.isInitOK) {
            values.put(BeanPropEnum.OrderConfig.initOK.toString(), 1)
        } else {
            values.put(BeanPropEnum.OrderConfig.initOK.toString(), 0)
        }
        return values
    }

    private fun getRecord(cursor: Cursor): DisplayConfigParaRecord {
        val record = DisplayConfigParaRecord()
        record.devphyid = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderConfig.devphyid.toString()))
        record.ip = cursor.getString(cursor.getColumnIndex(BeanPropEnum.OrderConfig.ip.toString()))
        record.port = cursor.getInt(cursor.getColumnIndex(BeanPropEnum.OrderConfig.port.toString()))
        val initOK = cursor.getInt(cursor.getColumnIndex(BeanPropEnum.OrderConfig.initOK.toString()))
        record.isInitOK = initOK == 1
        return record
    }
}