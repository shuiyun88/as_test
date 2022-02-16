package com.supwisdom.display.activities.display

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.TextView
import com.supwisdom.commonlib.utils.CommonUtil
import com.supwisdom.display.R
import com.supwisdom.display.activities.BaseActivity
import com.supwisdom.display.activities.display.adapter.DisplayTakeMealAdapter
import com.supwisdom.display.activities.display.adapter.DisplayWaitAdapter
import com.supwisdom.display.activities.init.InitActivity
import com.supwisdom.display.entity.DisplayMsgRecord
import com.supwisdom.display.utils.AppCommonUtil
import com.supwisdom.display.utils.AppExitUtil
import com.supwisdom.display.utils.PublicDef
import com.supwisdom.display.view.GridItemDecoration
import com.supwisdom.display.view.RecyclerViewUtil

/**
 * @author gqy
 * @version 1.0.1
 * @date 2019/1/3
 * @desc 叫号大屏
 */
class DisplayActivity : BaseActivity(), IDisplayView {
    private val TAG = "DisplayActivity"
    private lateinit var rvWaitOrder: RecyclerView
    private lateinit var rvTakeOrder: RecyclerView
    private lateinit var llHint: LinearLayout
    private lateinit var llNewTake: LinearLayout
    private lateinit var tvNewTakeOrder: TextView
    private lateinit var tvNewTakeWindowname: TextView
    private lateinit var tvNewTakePlease: TextView
    private lateinit var tvNewTakeHere: TextView
    private lateinit var tvNewTakeAway: TextView
    private lateinit var vHint: TextView
    private lateinit var tvWaitView: TextView
    private lateinit var tvTakeView: TextView
    private lateinit var waitOrderAdapter: DisplayWaitAdapter
    private lateinit var takeOrderAdapter: DisplayTakeMealAdapter
    private lateinit var presenter: DisplayPresenter
    private lateinit var animationAppear: AlphaAnimation
    private lateinit var animationDisappear: AlphaAnimation
    private var takeOrderShowNum = 0 //界面最大可显示数量
    private var waitOrderShowNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        initView()
    }

    override fun onResume() {
        super.onResume()
        AppCommonUtil.closeBar(this)
        presenter.refreshSocketConnect()
        presenter.loadOrderMsgList()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun screenSaver(flag: Boolean) {
        if (flag) {
            AppCommonUtil.setAppBrightness(this, PublicDef.SCREEN_SAVER_BRIGHTNESS)
        } else {
            AppCommonUtil.setAppBrightness(this, PublicDef.SCREEN_NORMAL_BRIGHTNESS)
        }
    }

    private fun initView() {
        presenter = DisplayPresenter(this)
        llHint = this.findViewById<LinearLayout>(R.id.ll_hint)
        vHint = this.findViewById<TextView>(R.id.v_hint)
        llNewTake = this.findViewById<LinearLayout>(R.id.ll_new_take_order)
        tvNewTakeOrder = this.findViewById<TextView>(R.id.tv_take_orderno)
        tvNewTakeWindowname = this.findViewById<TextView>(R.id.tv_take_window_name)
        tvNewTakePlease = this.findViewById<TextView>(R.id.tv_take_please)
        tvNewTakeHere = this.findViewById<TextView>(R.id.tv_take_here)
        tvNewTakeAway = this.findViewById<TextView>(R.id.tv_take_order_away)

        tvWaitView = findViewById<TextView>(R.id.v_wait_order)
        tvWaitView.setOnLongClickListener {
            AppExitUtil.exit()
            true
        }
        tvWaitView.setOnClickListener {
            presenter.resetHomeClearTime()
            jumpToActivity(InitActivity::class.java)
        }

        tvTakeView = findViewById<TextView>(R.id.v_take_order)
        tvTakeView.setOnLongClickListener {
            presenter.resetHomeClearTime()
            CommonUtil.showBar(this)
            CommonUtil.startSystemSetting(this)
            true
        }
        tvTakeView.setOnClickListener {
            presenter.testNewTakeDisplay()
        }
        rvWaitOrder = this.findViewById<RecyclerView>(R.id.wait_list)
        rvWaitOrder.layoutManager = GridLayoutManager(this, 2)
        rvWaitOrder.addItemDecoration(GridItemDecoration(10, 10))
        waitOrderAdapter = DisplayWaitAdapter(this)
        rvWaitOrder.adapter = waitOrderAdapter

        rvTakeOrder = this.findViewById<RecyclerView>(R.id.takefood_list)
        rvTakeOrder.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTakeOrder.addItemDecoration(RecyclerViewUtil.ItemDecoration(5, 5, 0, 0))
        takeOrderAdapter = DisplayTakeMealAdapter(this)
        rvTakeOrder.adapter = takeOrderAdapter

        animationAppear = AlphaAnimation(0.0f, 1.0f)
        animationAppear.duration = 3000
        animationDisappear = AlphaAnimation(1.0f, 0.0f)
        animationDisappear.duration = 3000
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun refreshOrderMsg(wait: List<DisplayMsgRecord>, take: List<DisplayMsgRecord>) {
        waitOrderAdapter.setList(wait)
        waitOrderAdapter.notifyDataSetChanged()
        takeOrderAdapter.setList(take)
        takeOrderAdapter.notifyDataSetChanged()
        refreshStatics()
    }

    override fun showNewWaitDisplay(record: DisplayMsgRecord) {
        waitOrderAdapter.insertItem(record)
        takeOrderAdapter.removeItem(record)
        refreshStatics()
    }

    override fun showNewTakeDisplay(record: DisplayMsgRecord) {
        waitOrderAdapter.removeItem(record)
        takeOrderAdapter.addItem(record)
        takeOrderAdapter.notifyDataSetChanged()
        refreshStatics()
        scheduleCurOrder(record)
    }

    override fun disTakeDisplay(record: DisplayMsgRecord) {
        waitOrderAdapter.removeItem(record)
        takeOrderAdapter.removeItem(record)
        refreshStatics()
    }

    override fun showConnectSuccess() {
        llHint.visibility = View.GONE
    }

    override fun showConnectFail(msg: String) {
        llHint.visibility = View.VISIBLE
        vHint.text = msg
    }

    override fun refreshWaitSort() {
        if (waitOrderShowNum == 0) {
            if (rvWaitOrder.childCount > 1) {
                val itemHeight = rvWaitOrder.getChildAt(0).measuredHeight + 10
                val height = rvWaitOrder.measuredHeight
                waitOrderShowNum = (height / itemHeight) * 2
            }
        }
        if (waitOrderShowNum < waitOrderAdapter.itemCount) {
            waitOrderAdapter.moveFirstToEnd()
            rvWaitOrder.scrollToPosition(0)
        }
    }

    override fun refreshTakeSort() {
        if (takeOrderShowNum == 0) {
            if (rvTakeOrder.childCount > 1) {
                val itemHeight = rvTakeOrder.getChildAt(0).measuredHeight + 10
                val height = rvTakeOrder.measuredHeight
                takeOrderShowNum = height / itemHeight
            }
        }
        if (takeOrderShowNum < takeOrderAdapter.itemCount) {
            takeOrderAdapter.moveFirstToEnd()
            rvTakeOrder.scrollToPosition(0)
        }
    }

    private fun refreshStatics() {
        tvWaitView.text = "备餐中 (${waitOrderAdapter.itemCount})"
        tvTakeView.text = "请取餐 (${takeOrderAdapter.itemCount})"
    }

    private var scheduleTask: ScheduleTaskTimer? = null
    private var turnIndex = 0
    private fun scheduleCurOrder(record: DisplayMsgRecord) {
        presenter.setAutoSort(false)
        tvNewTakeOrder.visibility = View.VISIBLE
        tvNewTakeWindowname.visibility = View.VISIBLE
        tvNewTakeHere.visibility = View.VISIBLE
        tvNewTakeAway.visibility = View.VISIBLE
        tvNewTakePlease.text = "请".plus(AppCommonUtil.getStuempnoSpec(record.custname))
        turnIndex = 0
        rvTakeOrder.visibility = View.GONE
        llNewTake.visibility = View.VISIBLE

        tvNewTakePlease.visibility = View.VISIBLE
        llNewTake.startAnimation(animationAppear)

        tvNewTakeOrder.text = record.orderno
        tvNewTakeWindowname.text = record.windowname

        resetTimerTask(2000)
    }

    private fun resetTimerTask(time: Long) {
        scheduleTask?.cancel()
        scheduleTask = ScheduleTaskTimer(time, 200)
        scheduleTask?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stopSocketConnect()
    }

    private inner class ScheduleTaskTimer
    (millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            turnIndex += 1
            when (turnIndex) {
                1 -> {
                    val scaleAni = ScaleAnimation(0.8f, 1.3f, 0.8f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    scaleAni.duration = 2000
                    scaleAni.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation) {

                        }

                        override fun onAnimationEnd(p0: Animation) {
                            tvNewTakeAway.startAnimation(scaleAni)
                        }

                        override fun onAnimationStart(p0: Animation) {

                        }
                    })
                    tvNewTakeOrder.startAnimation(scaleAni)
                    resetTimerTask(10 * 1000)
                }
                2 -> {
                    llNewTake.visibility = View.GONE
                    llNewTake.startAnimation(animationDisappear)
                    resetTimerTask(2000)
                }
                3 -> {
                    rvTakeOrder.visibility = View.VISIBLE

                    presenter.resetTakeShowFlag()
                    presenter.setAutoSort(true)
                }
            }
        }
    }
}
