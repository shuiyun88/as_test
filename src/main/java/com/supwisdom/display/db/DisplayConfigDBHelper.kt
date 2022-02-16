package com.supwisdom.display.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @author zzq
 * @date 2019/3/28
 * @desc 配置文件
 */
internal class DisplayConfigDBHelper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
    companion object {
        private const val DB_NAME = "display_config_db"
        private const val VERSION = 1
        /**
         * 本地配置文件
         */
        const val TABLE_NAME_CONFIG = "tb_para_config"

        private var mInstance: DisplayConfigDBHelper? = null
        fun getInstance(context: Context): DisplayConfigDBHelper {
            if (mInstance == null) {
                synchronized(DisplayConfigDBHelper::class.java) {
                    if (mInstance == null) {
                        mInstance = DisplayConfigDBHelper(context)
                    }
                }
            }
            return mInstance!!
        }
    }

    /**
     * SQL fro create table
     */
    private val CREATE_TABLE_NAME_CONFIG = ("create table IF NOT EXISTS "
            + TABLE_NAME_CONFIG + " ( "
            + BeanPropEnum.OrderConfig.id + " long primary key,"
            + BeanPropEnum.OrderConfig.devphyid + " varchar(64),"
            + BeanPropEnum.OrderConfig.ip + " varchar(128),"
            + BeanPropEnum.OrderConfig.port + " integer,"
            + BeanPropEnum.OrderConfig.initOK + " integer )")

    private val lock = ReentrantLock()
    fun getLock(): Lock {
        return lock
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_NAME_CONFIG)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            if (2 <= newVersion && oldVersion < 2) {
            }
        }
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}