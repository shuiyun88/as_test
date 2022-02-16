package com.supwisdom.display.view

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @author gqy
 * @date 2020/1/6
 * @since 1.0.0
 * @see
 * @desc  网格布局分割线。
 */
class GridItemDecoration(horizSpace: Int, verticalSpace: Int) : RecyclerView.ItemDecoration() {
    private val horizSpace = horizSpace
    private val verticalSpace = verticalSpace

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val childPosition = parent.getChildAdapterPosition(view)
        val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
        val offset = horizSpace / 2
        // 第一排，顶部不画
        if (childPosition < spanCount) {
            when {
                childPosition % spanCount == 0 -> // 最左边，左边不画
                    outRect.set(0, 0, offset, 0)
                childPosition % spanCount == spanCount - 1 -> //最右边，右边不画
                    outRect.set(offset, 0, 0, 0)
                else -> outRect.set(offset, 0, offset, 0)
            }
        } else {
            //上下的分割线，就从第二排开始，每个区域的顶部直接添加设定大小，不用再均分了
            when {
                childPosition % spanCount == 0 -> // 最左边，左边不画
                    outRect.set(0, verticalSpace, offset, 0)
                childPosition % spanCount == spanCount - 1 -> //最右边，右边不画
                    outRect.set(offset, verticalSpace, 0, 0)
                else -> outRect.set(offset, verticalSpace, offset, 0)
            }
        }
    }
}