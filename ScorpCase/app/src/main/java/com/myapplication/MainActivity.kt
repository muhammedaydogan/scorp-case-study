package com.myapplication

import DataSource
import FetchError
import FetchResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private var pullToRefreshRecyclerView: PullToRefreshRecyclerView? = null
    private val TAG: String = "MainActivityDebug"
    private var adapter: PeopleAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var progressBar: ProgressBar? = null
    private var progressBarIndeterminate: ProgressBar? = null


    // Simple MutexLock
    private var stillFetching: Boolean = true

    private var next: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = PeopleAdapter(mutableListOf())

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        pullToRefreshRecyclerView = findViewById(R.id.people_recycler_view)
        pullToRefreshRecyclerView?.layoutManager = linearLayoutManager
        pullToRefreshRecyclerView?.adapter = adapter

        progressBar = findViewById(R.id.progress)
        progressBarIndeterminate = findViewById(R.id.progress_indeterminate)

        pullToRefreshRecyclerView?.stillFetching = true
        fetch { size ->
//            Log.i(TAG, "size: $size")
            pullToRefreshRecyclerView?.stillFetching = false
        }

        pullToRefreshRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastItem: View? = recyclerView.getChildAt(recyclerView.childCount - 1)
                if (lastItem != null) {
//                    Log.i(tag,"onScrollStateChanged: ${lastItem.bottom + recyclerView.paddingBottom - recyclerView.height}")
                    // Instagram-like infinite scrolling feature
                    if (adapter?.getPeopleSize()!! <= linearLayoutManager!!.findLastVisibleItemPosition() + 5) {
                        if (!stillFetching) {
                            stillFetching = true
                            pullToRefreshRecyclerView?.stillFetching = true
                            fetch {
                                stillFetching = false
                                pullToRefreshRecyclerView?.stillFetching = false
                            }
                        }
                    }
                }
            }
        })

        pullToRefreshRecyclerView?.onPullListener =
            object : PullToRefreshRecyclerView.OnPullListener {
                override fun onPull(percent: Float) {
                    setProgressBar(percent)
                }

                override fun onRefresh(stillFetching: Boolean) {
                    refreshPeople(stillFetching)
                }

                override fun onCancel() {
                    progressBar?.visibility = View.GONE
                    progressBarIndeterminate?.visibility = View.GONE
                }
            }
    }

    private fun fetch(onComplete: (size: Int) -> Unit) {
//        Log.i(TAG, "fetch: $next")
        DataSource().fetch(
            next = next
        ) { fetchResponse: FetchResponse?, fetchError: FetchError? ->
            if (null != fetchError) {
                handleError(fetchError.errorDescription)
            } else if (null != fetchResponse) {
                if (fetchResponse.people.isEmpty()) {
                    noOneIsHere()
                } else {
                    adapter?.addPeople(fetchResponse.people)
                    next = fetchResponse.next
                    someoneIsHere()
                }
            }
            progressBarIndeterminate?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            onComplete(fetchResponse?.people?.size ?: 0)
        }
    }

    private fun handleError(errorDescription: String) {
        stillFetching = false
        adapter?.clearPeople(errorDescription)
        pullToRefreshRecyclerView?.error = true
    }

    private fun noOneIsHere() {
//        Log.i(TAG, "noOneIsHere: ")
        stillFetching = false
        adapter?.clearPeople("No one is here!")
        pullToRefreshRecyclerView?.noOne = true
    }

    private fun someoneIsHere() {
//        Log.i(TAG, "someoneIsHere: ")
        stillFetching = false
    }

    //! Progress is not working
    private fun setProgressBar(percent: Float) {
        progressBar?.visibility = View.VISIBLE
        progressBarIndeterminate?.visibility = View.GONE
        if (percent >= 1F) {
            progressBar?.alpha = 1F
            progressBar?.progress = 100
        } else {
            progressBar?.alpha = percent / 2
            progressBar?.progress = (percent).roundToInt()
        }
    }

    private fun refreshPeople(stillFetching: Boolean) {
        pullToRefreshRecyclerView?.noOne = false
        pullToRefreshRecyclerView?.error = false
        if (stillFetching) return
        progressBar?.visibility = View.GONE
        progressBarIndeterminate?.visibility = View.VISIBLE
        if (!this.stillFetching) {
            pullToRefreshRecyclerView?.stillFetching = true
            this.stillFetching = true
            adapter?.clearPeople()
            fetch {
                pullToRefreshRecyclerView?.stillFetching = false
                this.stillFetching = false
                progressBarIndeterminate?.visibility = View.GONE
            }
        }
    }
}
