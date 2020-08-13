package com.draco.extlaunch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private fun getAppList(): ArrayList<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = ArrayList<AppInfo>()
        for (app in activities) {
            val id = app.activityInfo.packageName

            if (id == packageName)
                continue

            val info = AppInfo()
            info.id = id
            info.name = app.activityInfo.loadLabel(packageManager).toString()
            info.img = packageManager.getApplicationIcon(id)

            appList.add(info)
        }

        appList.sortBy {
            it.name.toLowerCase(Locale.getDefault())
        }

        return appList
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(notificationChannelId, "Refocus", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.setItemViewCacheSize(250)

        val appInfoList = getAppList()
        val adapter = RecyclerAdapter(appInfoList, recycler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        createNotificationChannel()
    }

    override fun onDestroy() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        super.onDestroy()
    }
}