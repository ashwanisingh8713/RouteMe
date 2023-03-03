package com.route.routeme

import android.app.Application
import com.route.util.FileUtil

/**
 * Created by Ashwani Kumar Singh on 03,March,2023.
 */
class RouteApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Initializing Request writer file
        FileUtil.createFile(this)
    }
}