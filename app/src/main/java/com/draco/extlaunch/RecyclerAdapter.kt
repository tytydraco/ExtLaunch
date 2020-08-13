package com.draco.extlaunch

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
        private var appList: ArrayList<AppInfo>,
        private val recyclerView: RecyclerView
    ): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
        val name = itemView.findViewById(R.id.name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = appList[position]

        holder.img.setImageDrawable(info.img)
        holder.img.contentDescription = info.name
        holder.name.text = info.name

        holder.itemView.setOnClickListener {
            val dm = recyclerView.context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
            val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

            if (displays.isEmpty()) {
                Toast.makeText(recyclerView.context, "No external displays detected.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* Output to first usable display */
            var success = false
            for (display in displays.reversed()) {
                if (!display.isValid)
                    continue
                
                /* Start on external display */
                val externalAppIntent = Intent(recyclerView.context, AppLauncher::class.java)
                externalAppIntent.putExtra("appId", info.id)
                externalAppIntent.putExtra("displayId", display.displayId)
                val externalPendingIntent = PendingIntent.getBroadcast(recyclerView.context, 0, externalAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                recyclerView.context.sendBroadcast(externalAppIntent)

                /* Create intent for internal display */
                val internalAppIntent = Intent(recyclerView.context, AppLauncher::class.java)
                internalAppIntent.putExtra("appId", info.id)
                internalAppIntent.putExtra("displayId", 0)
                val internalPendingIntent = PendingIntent.getBroadcast(recyclerView.context, 1, internalAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                /* Create notification to resume */
                val notificationBuilder = NotificationCompat.Builder(recyclerView.context, notificationChannelId)
                    .setSmallIcon(R.drawable.ic_baseline_devices_24)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SYSTEM)
                    .setContentTitle(info.name)
                    .setContentText("Refocus or move ${info.name} between displays.")
                    .setLargeIcon(info.img?.toBitmap())
                    .setOngoing(true)
                    .addAction(R.drawable.ic_baseline_devices_24, "Internal", internalPendingIntent)
                    .addAction(R.drawable.ic_baseline_devices_24, "External", externalPendingIntent)
                NotificationManagerCompat.from(recyclerView.context).notify(0, notificationBuilder.build())

                success = true
                break
            }

            if (!success)
                Toast.makeText(recyclerView.context, "Displays are incompatible.", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnLongClickListener {
            /* Output to internal display */
            val internalAppIntent = Intent(recyclerView.context, AppLauncher::class.java)
            internalAppIntent.putExtra("appId", info.id)
            internalAppIntent.putExtra("displayId", 0)
            recyclerView.context.sendBroadcast(internalAppIntent)

            return@setOnLongClickListener true
        }
    }
}