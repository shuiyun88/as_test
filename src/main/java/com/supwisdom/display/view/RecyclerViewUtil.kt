package com.supwisdom.display.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @author zzq
 * @date 2018/6/21
 * @desc TODO
 */
class RecyclerViewUtil {
    class ItemDecoration constructor(top: Int, bottom: Int, left: Int, right: Int) : RecyclerView.ItemDecoration() {
        private val top = top
        private val bottom = bottom
        private val left = left
        private val right = right
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect!!.top = top
            outRect.left = left
            outRect.bottom = bottom
            outRect.right = right
        }
    }
}