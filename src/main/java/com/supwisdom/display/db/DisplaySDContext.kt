package com.supwisdom.display.db

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.IOException

/**
 * @author zzq
 * @date 2018/5/7.
 * @version 1.0.1
 * @desc  context重定向到SD卡
 */
internal class DisplaySDContext constructor(context: Context) : ContextWrapper(context) {

    override fun getDatabasePath(name: String): File? {
        if (android.os.Environment.MEDIA_MOUNTED == android.os.Environment.getExternalStorageState()) {
            //获取sd卡路径
            var dbDir = android.os.Environment.getExternalStorageDirectory().absolutePath
            dbDir += "/supwisdom"//数据库所在目录
            val dbPath = "$dbDir/$name"//数据库路径
            //判断目录是否存在，不存在则创建该目录
            val dirFile = File(dbDir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }
            val dbFile = File(dbPath)
            if (!dbFile.exists()) {
                try {
                    if (dbFile.createNewFile()) {
                        return dbFile
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                return dbFile
            }
        }
        return null
    }

    override fun openOrCreateDatabase(name: String, mode: Int,
                                      factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
        val file = getDatabasePath(name)
        return if (file == null) {
            super.openOrCreateDatabase(name, mode, factory)
        } else {
            SQLiteDatabase.openOrCreateDatabase(file, factory)
        }
    }

    override fun openOrCreateDatabase(name: String, mode: Int,
                                      factory: SQLiteDatabase.CursorFactory?, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
        val file = getDatabasePath(name)
        return if (file == null) {
            super.openOrCreateDatabase(name, mode, factory, errorHandler)
        } else {
            SQLiteDatabase.openOrCreateDatabase(file, factory)
        }
    }
}