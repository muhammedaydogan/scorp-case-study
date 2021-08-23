package com.myapplication

import DataSource
import FetchError
import FetchResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val tag: String = "MainActivityDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataSource().fetch(next = "12") { fetchResponse: FetchResponse?, fetchError: FetchError? ->
            Log.i(tag, "onCreate: ${fetchResponse ?: "null response"}")
            Log.i(tag, "onCreate: ${fetchError ?: "null error"}")
        }
    }
}