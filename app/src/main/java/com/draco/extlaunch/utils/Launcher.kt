package com.draco.extlaunch.utils

import android.app.ActivityOptions
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.View
import com.draco.extlaunch.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class Launcher(
    private val context: Context,
    private val view: View,
    private val appId: String
) {
    private fun getDisplays(): List<Display> {
        val dm = context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
        return dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION).filter {
            it.isValid
        }
    }

    private fun startActivityOn(display: Display) {
        val appIntent = context.packageManager.getLaunchIntentForPackage(appId) ?: return
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val bundle = ActivityOptions
            .makeBasic()
            .setLaunchDisplayId(display.displayId)
            .toBundle()

        try {
            context.startActivity(appIntent, bundle)
        } catch (e: Exception) {
            Snackbar.make(
                view,
                context.getString(R.string.snackbar_start_failed),
                Snackbar.LENGTH_SHORT
            )
                .setAction(context.getString(R.string.dismiss), null)
                .show()
        }
    }

    fun start() {
        val displays = getDisplays()
        if (displays.isEmpty()) {
            Snackbar.make(
                view,
                context.getString(R.string.snackbar_no_displays),
                Snackbar.LENGTH_SHORT
            )
                .setAction(context.getString(R.string.dismiss), null)
                .show()
            return
        }

        /* Launch on the only display */
        if (displays.size == 1) {
            startActivityOn(displays[0])
            return
        }

        /* Ask user where to launch */
        val displayNames = displays.mapNotNull {
            it.name
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.dialog_displays_title)
            .setItems(displayNames.toTypedArray()) { _, i ->
                startActivityOn(displays[i])
            }
            .show()
    }
}