package com.example.mypomadora

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import java.util.logging.Logger
import android.media.MediaPlayer
import android.provider.Settings


class BroadcastService: Service() {
    private val TAG = "BroadcastService"
    val broadcastIntent = Intent(COUNTDOWN_BR)
    lateinit var cdt: CountDownTimer
    lateinit var player: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this,  Settings.System.DEFAULT_RINGTONE_URI)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Starting timer")

        intent?.extras.let {
            var time = intent?.getLongExtra("timeInMilliSeconds", 0L)
            cdt = object : CountDownTimer(time!!, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    broadcastIntent.putExtra("countdown", millisUntilFinished)
                    sendBroadcast(broadcastIntent)
                }

                override fun onFinish() {
                    broadcastIntent.putExtra("finished", true)
                    sendBroadcast(broadcastIntent)
                    // play default sound
                    Log.i(TAG, "FINISHED")
                    player.isLooping = false
//                  player.start()
                }
            }
            cdt.start()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        if(::cdt.isInitialized) cdt.cancel()
        Log.i(TAG, "Timer cancelled")
        super.onDestroy()
    }

    companion object {
        var COUNTDOWN_BR = "com.example.mypomadora.CTRECEIVER"
    }
}