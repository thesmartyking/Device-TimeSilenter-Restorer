package com.example.timesilenterrestorer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartOnBoot: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent!!.action ||
            Intent.ACTION_POWER_CONNECTED == intent!!.action ||
            Intent.ACTION_BATTERY_OKAY == intent!!.action ||
            Intent.ACTION_BATTERY_LOW == intent!!.action ||
            Intent.ACTION_AIRPLANE_MODE_CHANGED == intent!!.action ||
            Intent.ACTION_POWER_DISCONNECTED == intent!!.action   ) {
            val i = Intent(context, TimeService::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startService(i)
        }
    }
}