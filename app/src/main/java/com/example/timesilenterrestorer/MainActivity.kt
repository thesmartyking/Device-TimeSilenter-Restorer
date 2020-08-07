package com.example.timesilenterrestorer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var onTimetxt:EditText
    private lateinit var offTimetxt:EditText
    private lateinit var setbtn:Button
    private lateinit var cancelbtn:Button
//    private lateinit var mAudioManager:AudioManager

    private val time_key="Timekey"
    private val strttime="StartTime"
    private val endtime="EndTime"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionbar = supportActionBar
        actionbar!!.title = "Time Silenter Scheduler"

        onTimetxt=findViewById(R.id.onTimeEditTxt)
        offTimetxt=findViewById(R.id.offTimeEditTxt)
        setbtn=findViewById(R.id.btnSet)
        cancelbtn=findViewById(R.id.btnCancel)
//        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        sharedPreferences = getSharedPreferences(time_key, Context.MODE_PRIVATE);
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

//        checkpermission()
        checksharedpreferences()
        onTimetxt.setOnClickListener {
            onTime_til.setError(null)
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
//                onTimetxt.setText("${SimpleDateFormat("hh:mm a").format(cal.time)}")
                onTimetxt.setText("${SimpleDateFormat("HH:mm").format(cal.time)}")
            }
//            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        offTimetxt.setOnClickListener {
            offTime_til.setError(null)
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                offTimetxt.setText("${SimpleDateFormat("HH:mm").format(cal.time)}")
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        setbtn.setOnClickListener {
            if(onTimetxt.text.trim().toString().isNotEmpty() && offTimetxt.text.trim().toString().isNotEmpty())
            {
                editor.clear()
                editor.apply()
                val start_time=onTimetxt.text.trim().toString()
                val end_time=offTimetxt.text.trim().toString()
                editor.putString(strttime,start_time)
                editor.putString(endtime,end_time)
                editor.commit()
//                checktime()
                checkpermission()
            }
            else if(onTimetxt.text.trim().toString().isEmpty() && offTimetxt.text.trim().toString().isNotEmpty())
            {
                onTime_til.setError("Enter Start Time")
            }
            else if(onTimetxt.text.trim().toString().isNotEmpty() && offTimetxt.text.trim().toString().isEmpty())
            {
                offTime_til.setError("Enter End Time")
            }
            else
            {
                onTime_til.setError("Enter Start Time")
                offTime_til.setError("Enter End Time")
            }

        }
        cancelbtn.setOnClickListener {
            stopService(Intent(applicationContext, TimeService::class.java))
            Toast.makeText(applicationContext, "Service running in Background--> Stopped", Toast.LENGTH_SHORT).show()

        }



    }
    private fun checksharedpreferences()
    {
        onTimetxt.setText(sharedPreferences.getString(strttime,""))
        offTimetxt.setText(sharedPreferences.getString(endtime,""))

    }

    private fun checkpermission()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                == PackageManager.PERMISSION_DENIED
                && checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)
                == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY,Manifest.permission.MODIFY_AUDIO_SETTINGS)
                requestPermissions(permissions, 100)
            }
            else{
                checktime()
            }
        }
        else{
            checktime()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checktime()
                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checktime()
    {
        val cal = Calendar.getInstance()
        val currenttime=SimpleDateFormat("HH:mm").format(cal.time).toString()
        Log.d("CT:-",currenttime)
        val start_time=onTimetxt.text.trim().toString()
        val end_time=offTimetxt.text.trim().toString()
        if (currenttime==start_time)
        {
//            startService(Intent(applicationContext, TimeService::class.java))
            val intenttimservice=Intent(applicationContext, TimeService::class.java)
//            intenttimservice.putExtra("StartTime",start_time)
//            intenttimservice.putExtra("EndTime",end_time)
            startService(intenttimservice)
            Toast.makeText(applicationContext, "Service running in Background--> Started", Toast.LENGTH_SHORT).show()

        }
        else
        {
            val intenttimservice=Intent(applicationContext, TimeService::class.java)
//            intenttimservice.putExtra("StartTime",start_time)
//            intenttimservice.putExtra("EndTime",end_time)
            startService(intenttimservice)
            Toast.makeText(this,"Wait! It Will Schedule on $start_time & End on $end_time",Toast.LENGTH_LONG).show()
        }



    }

}