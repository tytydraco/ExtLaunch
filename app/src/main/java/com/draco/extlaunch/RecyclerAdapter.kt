package com.draco.extlaunch

import android.app.ActivityOptions
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
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
        private val recyclerView: RecyclerView,
        private val packageManager: PackageManager,
        private val notificationBuilder: NotificationCompat.Builder
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
            val intent = packageManager.getLaunchIntentForPackage(info.id)
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val dm = recyclerView.context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
            val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

            if (displays.isEmpty()) {
                Toast.makeText(recyclerView.context, "No external displays detected.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* Output to first usable display */
            var success = false
            for (display in displays.reversed()) {
                try {
                    val options = ActivityOptions.makeBasic()
                    options.launchDisplayId = display.displayId
                    recyclerView.context.startActivity(intent, options.toBundle())

                    /* Create notification to resume */
                    val pendingIntent = PendingIntent.getActivity(recyclerView.context, 0, intent, 0)
                    notificationBuilder
                        .setContentIntent(pendingIntent)
                        .setContentTitle(info.name)
                        .setContentText("Tap to refocus ${info.name} to the external display.")
                        .setLargeIcon(info.img?.toBitmap())
                    NotificationManagerCompat.from(recyclerView.context).notify(0, notificationBuilder.build())

                    success = true
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }
            }

            if (!success)
                Toast.makeText(recyclerView.context, "Displays are incompatible.", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnLongClickListener {
            val intent = packageManager.getLaunchIntentForPackage(info.id)
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

            /* Output to internal display */
            val options = ActivityOptions.makeBasic()
            options.launchDisplayId = 0
            recyclerView.context.startActivity(intent, options.toBundle())

            return@setOnLongClickListener true
        }
    }
}