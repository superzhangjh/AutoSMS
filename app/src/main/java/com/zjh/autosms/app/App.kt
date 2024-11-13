package com.zjh.autosms.app

import android.app.Activity
import android.app.Application

class App : Application() {
    companion object {
        private lateinit var app: App

        fun setApp(app: App) {
            this.app = app
        }

        fun getApp() = app
    }

    override fun onCreate() {
        super.onCreate()
        setApp(this)
    }
}

fun Activity.getApp() = application as App