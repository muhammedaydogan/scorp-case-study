package com.myapplication

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PullToRefreshRecyclerView : RecyclerView {
    var noOne: Boolean = false
    var error: Boolean = false
    private var TAG: String = "PullToRefreshRV"

    private var firstVisibleItemPosition: Int = 0
    private var eventStart: Float = 0F
    var stillFetching: Boolean = false
    var startAnimation: Boolean = false

    var onPullListener: OnPullListener? = null

    constructor(context: Context) : super(context, null) {
        init();
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init();
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init();
    }

    private fun init() {

    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        //todo:
        if (e != null) {
            Log.i(TAG, "onTouchEvent: ")
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (firstVisibleItemPosition == 0 || noOne || error) {
//                        Log.i(tag, "onTouchEvent: $firstVisibleItemPosition")
                        eventStart = e.y
                    }
                }
                MotionEvent.ACTION_UP -> {
//                    Log.i(tag, "onTouchEvent: UP: ${e.y}")
                    startAnimation = false
                    if (firstVisibleItemPosition == 0 || noOne || error) {
                        if (e.y - eventStart >= 300) {
                            if (!stillFetching)
                                onPullListener?.onRefresh(stillFetching)
                            return false
                        } else
                            onPullListener?.onCancel()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    firstVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (firstVisibleItemPosition == 0 || noOne || error) {
                        if (!startAnimation) {
                            eventStart = e.y
                            startAnimation = true
                        }
                        if (e.y - eventStart > 0) {
                            onPullListener?.onPull(((e.y - eventStart) / 300))
                            return false
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(e)
    }

    interface OnPullListener {
        fun onPull(percent: Float)
        fun onRefresh(stillFetching: Boolean)
        fun onCancel()
    }
}