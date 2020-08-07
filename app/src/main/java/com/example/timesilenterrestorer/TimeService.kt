package com.example.timesilenterrestorer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
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

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.i("onStart", "onStart")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)
//        Toast.makeText(applicationContext, "This is a Service running in Background", Toast.LENGTH_SHORT).show()
        val cal = Calendar.getInstance()
        val currenttime= SimpleDateFormat("HH:mm").format(cal.time).toString()
//        val start_time=intent.getStringExtra("StartTime")
        sharedPreferences = getSharedPreferences(time_key, Context.MODE_PRIVATE);
        val start_time=sharedPreferences.getString(strttime,"")!!
        val end_time=sharedPreferences.getString(endtime,"")!!
//        val end_time=intent.getStringExtra("EndTime")
        /*Log.d("S:- ",start_time)
        Log.d("E:- ",end_time)
        Log.d("Time:- ",currenttime)*/

//        Log.d("Stime:- ",start_time )

//       do {
//        while (currenttime == end_time) {
            if (currenttime == start_time) {
                mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//                mAudioManager.setRingerMode(0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
//            mAudioManager.ringerMode=0
//            mAudioManager.adjustAudio(true)

            }
           if (currenttime == end_time) {
                mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 2, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0)
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15, 0)
//            mAudioManager.ringerMode=15
            }
//       }while (currenttime == end_time)
//        }
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