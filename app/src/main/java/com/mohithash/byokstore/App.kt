package com.mohithash.byokstore

import android.app.Application
import com.mohithash.byokstore.data.JsonStore

class App : Application() {
    lateinit var store: JsonStore
    override fun onCreate() { super.onCreate(); store = JsonStore(this) }
}
