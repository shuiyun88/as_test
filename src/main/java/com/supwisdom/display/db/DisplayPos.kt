package com.supwisdom.display.db

import android.content.Context
import com.supwisdom.display.entity.DisplayConfigParaRecord
import com.supwisdom.display.entity.DisplayMsgRecord

/**
 * @author zzq
 * @date 2018/4/4.
 * @version 1.0.1
 * @desc  数据库操作方法
 */
internal class DisplayPos constructor(context: Context) {
    private var context: Context? = null
    //    private var sdContext: DisplaySDContext? = null
    private var displayConfigParaRecord: DisplayConfigParaRecord? = null
    private var displayConfigParaDao: DisplayConfigParaDao
    private var displayMsgDao: DisplayMsgDao

    init {
        this.context = context
//        this.sdContext = DisplaySDContext(context)
        /**
         * 存储在APK中
         */
        displayConfigParaDao = DisplayConfigParaDao(context)
        /**
         * 存储在SD卡
         */
        displayMsgDao = DisplayMsgDao(context)
    }

    fun getConfigPara(): DisplayConfigParaRecord? {
        if (displayConfigParaRecord == null) {
            try {
                displayConfigParaDao.getLock().lock()
                displayConfigParaRecord = displayConfigParaDao.get()
            } finally {
                displayConfigParaDao.getLock().unlock()
            }
        }
        return displayConfigParaRecord
    }

    fun replaceConfigPara(record: DisplayConfigParaRecord): Boolean {
        try {
            displayConfigParaDao.getLock().lock()
            if (displayConfigParaDao.replace(record)) {
                displayConfigParaRecord = record
                return true
            }
        } finally {
            displayConfigParaDao.getLock().unlock()
        }
        return false
    }

    fun replaceMsgRecord(record: DisplayMsgRecord): Boolean {
        try {
            displayMsgDao.getLock().lock()
            return displayMsgDao.replace(record)
        } finally {
            displayMsgDao.getLock().unlock()
        }
    }

    fun clearMsgRecord(): Boolean {
        try {
            displayMsgDao.getLock().lock()
            return displayMsgDao.clear()
        } finally {
            displayMsgDao.getLock().unlock()
        }
    }

    fun getAllMsgRecord(): List<DisplayMsgRecord> {
        try {
            displayMsgDao.getLock().lock()
            return displayMsgDao.getAll()
        } finally {
            displayMsgDao.getLock().unlock()
        }
    }

}