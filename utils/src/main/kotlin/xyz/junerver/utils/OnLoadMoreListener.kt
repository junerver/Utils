package xyz.junerver.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author Junerver
 * @Date 2019/4/12-10:03
 * @Email junerver@gmail.com
 * @Version v1.0
 * @Description
 */
private const val TAG = "OnLoadMoreListener"

abstract class OnLoadMoreListener : RecyclerView.OnScrollListener() {
    private lateinit var layoutManager: LinearLayoutManager
    private var itemCount = 0
    private var lastPosition = 0
    private var loadEnable = true
    abstract fun onLoadMore()

    //rv 的滚动方向，在第一次滑动的时候可以判断，初始状态值为-1
    //如果可以第一次滑动回调时，可以上滑动不可以左滑动。说明该rv的滑动方向为垂直方向 反之 则为 水平方向
    private var orientation = -1

    /**
     * 允许加载更多，在回调函数中处理
     */
    fun enableLoadMore() {
        loadEnable = true
    }

    /**
     * Description:
     * @author Junerver
     * @date: 2021/12/22-8:20
     * @Email: junerver@gmail.com
     * @Version: v1.0
     * @param dx 水平滚动距离，dx > 0 时为手指向左滚动,列表滚动显示右面的内容，dx < 0 时为手指向右滚动,列表滚动显示左面的内容
     * @param dy 垂直滚动距离，dy > 0 时为手指向上滚动,列表滚动显示下面的内容，dy < 0 时为手指向下滚动,列表滚动显示上面的内容
     * @return
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (orientation == -1) {
            //未初始化方向
            if (recyclerView.canScrollVertically(1) && !recyclerView.canScrollHorizontally(1)) {
                orientation = RecyclerView.VERTICAL
            } else if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollHorizontally(1)) {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        if (recyclerView.layoutManager is LinearLayoutManager) {
            layoutManager = recyclerView.layoutManager as LinearLayoutManager
            //当前列表中全部的item数量
            itemCount = layoutManager.itemCount
            //当前最后一个完全可见的item的位置标号
            lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            //通过lm的布局方向判断是否在该方向正向移动
            val isScroll =
                if (layoutManager.orientation == RecyclerView.HORIZONTAL) dx > 0 else dy > 0
            if (lastPosition >= itemCount - 2 && isScroll) {
                if (loadEnable) {
                    loadEnable = false
                    onLoadMore() //在回调中处理
                }
            }
        } else {
            //传入正值检测是否为手指上划，如果此视图可以在指定方向滚动，则返回值为true，否则为false
            val cannotScroll = if (orientation == RecyclerView.HORIZONTAL) {
                !recyclerView.canScrollHorizontally(1)
            } else {
                !recyclerView.canScrollVertically(1)
            }
            //rv此时不可滑动，判断是否加载更多
            if (cannotScroll) {
                if (loadEnable) {
                    //每次回调执行后自动关闭回调，需要手动判断允许回调
                    loadEnable = false
                    //在回调中处理
                    onLoadMore()
                }
            }
        }
    }
}