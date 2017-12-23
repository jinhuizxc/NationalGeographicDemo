package com.example.jh.nationalgeographic.ui.listener

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

/**
 * Created by wheat7 on 2017/9/13.
 */
abstract class LoadMoreRecyclerOnScrollListener(private val mLayoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var isLoading: Boolean = false
    //list到达 最后一个item的时候会使用到这个变量, 触发加载
    private val visibleThreshold = 1
    // The minimum amount of items to have below your current scroll position before loading more.
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    //默认第一页
    private var current_page = 1

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView!!.childCount
        // is 的用法似乎时instance of 的感觉
        /**
         * visibleItemCount =4
         * totalItemCount =15
         * firstVisibleItem =11
         * 这点要理解，当拉到最后的时候第一个可见的item是11，列表最多13，
         * 加上"正在加载更多" 才是14，
         * totalItemCount = visibleItemCount + firstVisibleItem
         */

        if (mLayoutManager is LinearLayoutManager) {
            totalItemCount = mLayoutManager.itemCount
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
        }
        // 待会测试下这个布局
        if (mLayoutManager is StaggeredGridLayoutManager) {
            totalItemCount = mLayoutManager.itemCount
            val lastPositions = mLayoutManager
                    .findFirstVisibleItemPositions(IntArray(mLayoutManager
                            .spanCount))
            firstVisibleItem = getMinPositions(lastPositions)
        }

        //判断加载完成
        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false
                previousTotal = totalItemCount
            }
        }
        // 判断是否加载更多的判断
        if (!isLoading && totalItemCount > visibleItemCount && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            current_page++
            // 执行加载更多的方法
            onLoadMore(current_page)
            isLoading = true
            //loadMore(current_page);
        }
    }

    /**
     * 获得当前展示最小的position

     * @param positions
     * *
     * @return
     */
    private fun getMinPositions(positions: IntArray): Int {
        val size = positions.size
        var minPosition = Integer.MAX_VALUE
        // i in 0..size - 1 提示建议换成 i in 0 until size
        for (i in 0 until size) {
            minPosition = Math.min(minPosition, positions[i])
        }
        return minPosition
    }

    // 抽象方法，必须实现
    abstract fun onLoadMore(current_page: Int)
}
