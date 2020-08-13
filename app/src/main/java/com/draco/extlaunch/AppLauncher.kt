package com.draco.extlaunch

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppLauncher: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appId = intent.getStringExtra("appId")!!
        val displayId = intent.getIntExtra("displayId", 0)

        val appIntent = context.packageManager.getLaunchIntentForPackage(appId)
        appIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val options = ActivityOptions.makeBasic()
        options.launchDisplayId = displayId
        try {
            context.startActivity(appIntent, options.toBundle())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}