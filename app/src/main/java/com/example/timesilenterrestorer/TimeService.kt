package com.example.timesilenterrestorer

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class TimeService :Service(){

    private val time_key="Timekey"
    private val strttime="StartTime"
    private val endtime="EndTime"
    private lateinit var mAudioManager: AudioManager
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val runTime = 2000

    override fun onCreate() {
        super.onCreate()
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        Log.i("TimeService", "onCreate")
        handler = Handler()
        runnable = Runnable { handler.postDelayed(runnable, runTime.toLong()) }
        handler.post(runnable)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ondestroy_create_notification()
    {
        val NOTIFICATION_CHANNEL_ID = "com.example.timesilenterrestorer"
        val channelName = "Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.RED
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
//        val notificationIntent = Intent(this, MainActivity::class.java)
        val inclick = "Click on Notification to Run Service"
        val notificationIntent = Intent(applicationContext, TimeService::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
        val notification=NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_clock_in_placeholder_shape_svgrepo_com)
            .setAutoCancel(false)
            .setContentTitle("Device Silenter App --> ON")
            .setContentText(inclick)
            .setContentIntent(pendingIntent)
            .build()
//        startForeground(1337, notification)
        startForeground(1, notification)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.i("onStart", "onStart")
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)
//        Toast.makeText(applicationContext, "This is a Service running in Background", Toast.LENGTH_SHORT).show()
        val cal = Calendar.getInstance()
        val currenttime= SimpleDateFormat("HH:mm").format(cal.time).toString()

        sharedPreferences = getSharedPreferences(time_key, Context.MODE_PRIVATE);
        val start_time=sharedPreferences.getString(strttime,"")!!
        val end_time=sharedPreferences.getString(endtime,"")!!

        if (currenttime == start_time) {
                mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
        }
        if (currenttime == end_time) {
                mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 2, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15, 0)
        }
//        ondestroy_create_notification()
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
//        Return the communication channel to the service.
//        throw UnsupportedOperationException("Not yet implemented")
        return null
    }
    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }
}