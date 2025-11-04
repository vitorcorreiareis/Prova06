package com.example.prova06

import android.app.Application
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession

class ImobiControlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        LocalSession.restore(this)
    }
}
