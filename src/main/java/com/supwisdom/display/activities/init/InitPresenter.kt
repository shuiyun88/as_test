package com.supwisdom.display.activities.init

import com.supwisdom.commonlib.execption.TerminalConfigError
import com.supwisdom.commonlib.utils.ThreadPool
import com.supwisdom.display.Pos
import com.supwisdom.display.entity.DisplayConfigParaRecord

/**
 * @author gqy
 * @version 1.0.1
 * @date 2018/12/11
 * @desc init
 */
class InitPresenter constructor(private val initView: InitView) {
    private val pos = Pos.getInstance()
    fun saveOrderConfig(config: DisplayConfigParaRecord) {
        ThreadPool.getShortPool().execute {
            try {
                if (!pos.replaceConfigPara(config)) {
                    throw  TerminalConfigError("保存配置参数失败")
                }
                initView.getActivity().runOnUiThread {
                    initView.saveConfigSuc()
                }
            } catch (ex: TerminalConfigError) {
                initView.getActivity().runOnUiThread {
                    initView.showToast(ex.message ?: "null")
                }
            }
        }
    }

}