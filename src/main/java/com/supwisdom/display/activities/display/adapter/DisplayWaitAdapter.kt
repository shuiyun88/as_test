package com.supwisdom.display.activities.display.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.supwisdom.display.R
import com.supwisdom.display.entity.DisplayMsgRecord

/**
 * 备餐中订单
 */
@Suppress("DEPRECATION")
class DisplayWaitAdapter constructor(private val context: Context) : RecyclerView.Adapter<DisplayWaitAdapter.Holder>() {
    private val list = ArrayList<DisplayMsgRecord>()

    fun setList(data: List<DisplayMsgRecord>?) {
        list.clear()
        if (data != null && data.isNotEmpty()) {
            list.addAll(data)
        }
    }

    /**
     * 插在结尾
     */
    fun insertItem(bean: DisplayMsgRecord) {
        if (checkRepeat(bean)) {
            return
        }
        list.add(bean)
        notifyItemInserted(list.size - 1)
    }

    fun removeItem(bean: DisplayMsgRecord): Boolean {
        for (index in 0 until list.size) {
            if (list[index].billno == bean.billno) {
                list.removeAt(index)
                notifyItemRemoved(index)
                return true
            }
        }
        return false
    }

    fun moveFirstToEnd(): Boolean {
        if (list.size > 1) {
            val bean = list[0]
            notifyItemChanged(0)
            list.remove(bean)
            list.add(bean)
            notifyItemMoved(0, list.size - 1)
        }
        return false
    }

    fun moveItemEnd(bean: DisplayMsgRecord) {
        for (index in 0 until list.size) {
            if (list[index].billno == bean.billno) {
                notifyItemChanged(index)
                list.remove(bean)
                list.add(bean)
                notifyItemMoved(index, list.size - 1)
                break
            }
        }
    }

    private fun checkRepeat(bean: DisplayMsgRecord): Boolean {
        for (index in 0 until list.size) {
            if (list[index].billno == bean.billno) {
                //说明已存在，防止反复插入
                notifyItemChanged(index)
                return true
            }
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_display_wait, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bean = list[position]
        holder.tvOrderno.text = "${bean.orderno}"
    }

    inner class Holder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvOrderno = itemView.findViewById(R.id.tv_orderno) as TextView
    }
}