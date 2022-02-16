package com.supwisdom.display.activities.init

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.supwisdom.display.Pos
import com.supwisdom.display.R
import com.supwisdom.display.activities.BaseActivity
import com.supwisdom.display.activities.display.DisplayActivity
import com.supwisdom.display.entity.DisplayConfigParaRecord
import com.supwisdom.display.utils.AppCommonUtil
import com.supwisdom.display.utils.ToastUtil

/**
 * @author gqy
 * @version 1.0.1
 * @date 2019/1/3
 * @desc 初始化
 */
class InitActivity : BaseActivity(), InitView {
    private lateinit var vParaTermno: EditText
    private lateinit var vParaIp: EditText
    private lateinit var vParaPort: EditText
    private lateinit var presenter: InitPresenter
    private val pos = Pos.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        initView()
        initData()
    }

    private fun initData() {
        presenter = InitPresenter(this)
        var config = pos.getConfigPara()
        if (config == null) {
            config = DisplayConfigParaRecord()
            config.devphyid = "90300001"
            config.ip = "192.168.1.1"
            config.port = 8080
            pos.replaceConfigPara(config)
        }
        vParaTermno.setText(config.devphyid)
        vParaIp.setText(config.ip)
        vParaPort.setText(config.port.toString())
    }

    private fun initView() {
        findViewById<TextView>(R.id.panel_back).setOnClickListener { finish() }
        vParaTermno = findViewById<EditText>(R.id.init_param_termno)
        vParaIp = findViewById<EditText>(R.id.init_param_ip)
        vParaPort = findViewById<EditText>(R.id.init_param_port)

        val vTitle = findViewById<TextView>(R.id.tv_title)
        vTitle.text = "参数配置"

        val tvSave = findViewById<TextView>(R.id.tv_save)
        tvSave.visibility = View.VISIBLE
        tvSave.text = "完成配置"
        tvSave.background = ContextCompat.getDrawable(this, R.drawable.select_ordinary_consume_yellow)
        tvSave.setOnClickListener {
            saveCfgParam()
        }
    }

    override fun onResume() {
        super.onResume()
        AppCommonUtil.showBar(this)
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun showToast(msg: String) {
        ToastUtil.show(this, msg)
    }

    override fun saveConfigSuc() {
        jumpToActivity(DisplayActivity::class.java)
        finish()
    }

    private fun saveCfgParam() {
        val devphyid = vParaTermno.text.toString().trim()
        if (TextUtils.isEmpty(devphyid)) {
            return ToastUtil.show(this, "终端号不能为空")
        }
        val ip = vParaIp.text.toString().trim()
        if (TextUtils.isEmpty(ip)) {
            return ToastUtil.show(this, "IP不能为空")
        }
        if (ip.startsWith("http")) {
            return ToastUtil.show(this, "服务IP不能以http开头")
        }
        val port = vParaPort.text.toString().trim()
        if (TextUtils.isEmpty(port)) {
            return ToastUtil.show(this, "端口不能为空")
        }
        if (port.toInt() > 0xFFFF) {
            return ToastUtil.show(this, "端口不能大于65535")
        }
        val config = pos.getConfigPara() ?: DisplayConfigParaRecord()
        config.devphyid = devphyid
        config.ip = ip
        config.port = port.toInt()
        config.isInitOK = true
        presenter.saveOrderConfig(config)
    }
}

