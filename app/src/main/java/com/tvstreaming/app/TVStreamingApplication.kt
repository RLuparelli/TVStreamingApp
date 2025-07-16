package com.tvstreaming.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TVStreamingApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize any required services or configurations
        initializeApp()
    }
    
    private fun initializeApp() {
        // TODO: Add any initialization logic here
        // For example: crash reporting, analytics, etc.
    }
}