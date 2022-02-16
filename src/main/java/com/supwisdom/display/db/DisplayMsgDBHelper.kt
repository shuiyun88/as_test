package com.supwisdom.display.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @author gqy
 * @date 2019/1/7.
 * @version 1.0.1
 * @desc  流水数据库
 */
internal class DisplayMsgDBHelper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
    companion object {
        private const val DB_NAME = "display_msg_db"
        private const val VERSION = 2
        const val TABLE_NAME_ORDERMSG = "tb_msg_order"

        private var mInstance: DisplayMsgDBHelper? = null
        fun getInstance(context: Context): DisplayMsgDBHelper {
            if (mInstance == null) {
                synchronized(DisplayMsgDBHelper::class.java) {
                    if (mInstance == null) {
                        mInstance = DisplayMsgDBHelper(context)
                    }
                }
            }
            return mInstance!!
        }
    }


    private val lock = ReentrantLock()
    fun getLock(): Lock {
        return lock
    }


    private val CREATE_TABLE_NAME_ORDERDTL = ("create table IF NOT EXISTS "
            + TABLE_NAME_ORDERMSG + " ( "
            + BeanPropEnum.OrderMsg.billno + " varchar(64) primary key,"
            + BeanPropEnum.OrderMsg.windowid + " varchar(64),"
            + BeanPropEnum.OrderMsg.mealid + " integer,"
            + BeanPropEnum.OrderMsg.windowname + " varchar(64),"
            + BeanPropEnum.OrderMsg.custname + " varchar(32),"
            + BeanPropEnum.OrderMsg.transdate + " varchar(8),"
            + BeanPropEnum.OrderMsg.transtime + " varchar(6),"
            + BeanPropEnum.OrderMsg.takemealstatus + " varchar(1),"
            + BeanPropEnum.OrderMsg.orderno + " varchar(5))")
    private val DROP_TABLE_NAME_ORDERDTL = ("drop table IF EXISTS $TABLE_NAME_ORDERMSG")

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_NAME_ORDERDTL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            if (2 <= newVersion && oldVersion < 2) {
                db.execSQL(DROP_TABLE_NAME_ORDERDTL)
                db.execSQL(CREATE_TABLE_NAME_ORDERDTL)
            }
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}