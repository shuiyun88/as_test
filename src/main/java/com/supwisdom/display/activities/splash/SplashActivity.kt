package com.supwisdom.display.activities.splash

import android.os.Bundle
import com.supwisdom.commonlib.execption.TerminalConfigError
import com.supwisdom.commonlib.utils.ThreadPool
import com.supwisdom.display.R
import com.supwisdom.display.activities.BaseActivity
import com.supwisdom.display.activities.init.InitActivity
import com.supwisdom.display.activities.display.DisplayActivity
import com.supwisdom.display.utils.AppCommonUtil

/**
 * @author gqy
 * @version 1.0.1
 * @date 2019/1/3
 * @desc 闪屏页
 */
class SplashActivity : BaseActivity() {
    private lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initData()
    }

    private fun initData() {
        presenter = SplashPresenter()
    }

    override fun onResume() {
        super.onResume()
        AppCommonUtil.closeBar(this)
        loadPara()
    }

    private fun loadPara() {
        ThreadPool.getShortPool().execute {
            try {
                presenter.loadConfig()
                runOnUiThread {
                    jumpToActivity(DisplayActivity::class.java)
                }
            } catch (ex: TerminalConfigError) {
                runOnUiThread {
                    jumpToActivity(InitActivity::class.java)
                }
            }
        }
    }
}
