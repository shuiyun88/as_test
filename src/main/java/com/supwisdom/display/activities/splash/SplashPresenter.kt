package com.supwisdom.display.activities.splash

import com.supwisdom.commonlib.execption.TerminalConfigError
import com.supwisdom.display.Pos

/**
 * @author gqy
 * @date 2020/1/3
 * @since 1.0.0
 */
class SplashPresenter {

    /**
     * @throws TerminalConfigError
     * @desc 加载通讯参数
     */
    fun loadConfig() {
        val pos = Pos.getInstance()
        val cfgRecord = pos.getConfigPara() ?: throw TerminalConfigError("未注册")
        if (!cfgRecord.isInitOK) {
            throw TerminalConfigError("未注册")
        }
    }
}