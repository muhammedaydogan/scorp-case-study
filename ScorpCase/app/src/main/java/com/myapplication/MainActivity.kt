package com.myapplication

import DataSource
import FetchError
import FetchResponse
import Person
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val tag: String = "MainActivityDebug"
    private lateinit var people: MutableList<Person>
    private lateinit var adapter: PeopleAdapter

    private var scrollState: Int? = RecyclerView.SCROLL_STATE_IDLE;
    private var lastScroll: Int = 0

    // Simple MutexLock
    private var stillFetching: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        people = mutableListOf()
        adapter = PeopleAdapter(people)

        recyclerView = findViewById(R.id.people_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        fetch { size ->
            Log.i(tag, "size: $size")
            stillFetching = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastItem: View? = recyclerView.getChildAt(recyclerView.childCount - 1)
                if (lastItem != null) {
//                    Log.i(
//                        tag,
//                        "onScrollStateChanged: ${lastItem.bottom + recyclerView.paddingBottom - recyclerView.height}"
//                    )

                    // Instagram-like infinite scrolling
//                    if (lastItem.bottom + recyclerView.paddingBottom - recyclerView.height == 0) {
//                        Log.i(tag, "onScrollStateChanged: Scroll End reached")
                    if (lastItem.bottom + recyclerView.paddingBottom - recyclerView.height < 150) {
                        if (!stillFetching) {
                            stillFetching = true
                            fetch { stillFetching = false }
                        }
//                    } else if () {
//                        when (newState) {
//                            RecyclerView.SCROLL_STATE_IDLE -> {
//                                Log.i(tag, "onScrollStateChanged: idle")
//                            }
//                            RecyclerView.SCROLL_STATE_DRAGGING -> {
//                                Log.i(tag, "onScrollStateChanged: dragging")
//                            }
//                            RecyclerView.SCROLL_STATE_SETTLING -> {
//                                Log.i(tag, "onScrollStateChanged: settling")
//                            }
//                        }
                    }
                }

                scrollState = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastScroll = dx
            }
        })
    }

    private fun fetch(onComplete: (size: Int) -> Unit) {
        DataSource().fetch(
            next = Random.nextInt(15, 16)
                .toString() // + "asd" // uncomment this to check what happens if there's an error
        ) { fetchResponse: FetchResponse?, fetchError: FetchError? ->
            if (null != fetchError) {
                Log.i(tag, "fetchError: $fetchError")
                Toast.makeText(this, fetchError.errorDescription, Toast.LENGTH_SHORT).show()
            } else if (null != fetchResponse) {
                Log.i(tag, "fetchResponse: $fetchResponse")
                adapter.addPeople(fetchResponse.people)
                onComplete(fetchResponse.people.size)
            } else {
                Log.w(tag, "Assume internal server error from DataSource")
                onComplete(0)
            }
        }
    }
}