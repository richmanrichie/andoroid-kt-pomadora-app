package com.example.mypomadora

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.mypomadora.Utils.getMilliSecondsFromSeparatedTime
import com.example.mypomadora.Utils.getSeparatedTime
import com.example.mypomadora.dialogs.TimeInputBottomSheet
import com.example.mypomadora.models.CallBackListener
import com.example.mypomadora.models.SeparatedTime
import com.example.mypomadora.models.TimeSelected
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), CallBackListener<TimeSelected?> {
    private val TAG = "MainActivity"
    private var SHARED_PREF_KEY = "TimeSharedPref"

    private var state = TickStates.PAUSE

    private var hour = "00"
    private var minute = "00"
    private var seconds = "00"

    lateinit var tvHour: TextView
    lateinit var tvMin: TextView
    lateinit var tvSecs: TextView

    var timeInMilli = 0L

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(SHARED_PREF_KEY, MODE_PRIVATE)
        setupUI()
        setupState()
        setTimeForGUI()

    }

    private fun setupUI() {
        tvHour = tHours
        tvSecs = tSecs
        tvMin = tMins

        toolbarBtn.setOnClickListener {
            TimeInputBottomSheet(this).apply {
                show(supportFragmentManager, TimeInputBottomSheet.TAG)
            }
        }

        progressBarButton.setOnClickListener {
            togglePlay()
            setupState()
        }


    }

    override fun onSelect(data: TimeSelected?) {
        if(data != null) {
            //save the new time
            sharedPreferences.edit().putLong(POMADORA_TIME, data.timeInMilliSeconds)
            sharedPreferences.edit().putLong(SHORT_TIME, data.shortBreakInMilliSeconds)
            sharedPreferences.edit().putLong(LONG_TIME, data.longBreakInMilliSeconds)
            sharedPreferences.edit().putInt(LONG_TIME, data.iterations)

            updateGUI(data.timeInMilliSeconds / 1000)
        }
    }

    private fun togglePlay() {
        state = if (state == TickStates.PLAY) {
            stopService(Intent(this, BroadcastService::class.java))
            TickStates.PAUSE
        } else {
            val intent = Intent(this, BroadcastService::class.java)
            intent.putExtra("timeInMilliSeconds", timeInMilli)
            startService(intent)
            TickStates.PLAY
        }
    }

    private fun setupState() {
        val button: ImageView = progressBarButton
        if (state == TickStates.PAUSE) {
            button.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else if(state == TickStates.PLAY) {
            button.setImageResource(R.drawable.ic_baseline_pause_24)
        }
        else {
            button.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, IntentFilter(BroadcastService.COUNTDOWN_BR))
        Log.i(TAG, "Registered broadcast receiver")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
        Log.i(TAG, "Registered broadcast receiver")
    }

    override fun onStop() {
        try {
            unregisterReceiver(broadcastReceiver)
        }catch (e: Exception) {

        }
        super.onStop()
    }

    override fun onDestroy() {
        stopService(Intent(this, BroadcastService::class.java))
        Log.i(TAG, "Stopped service broadcast receiver")
        super.onDestroy()

    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setTimeForGUI(intent)
        }
    }

    private fun setTimeForGUI(intent: Intent? = null) {
        var time = 0
        if(intent?.extras != null) {
            val millisUntilFinished = intent.getLongExtra("countdown", 0)
            val inTime = millisUntilFinished / 1000
            updateGUI(inTime)

            if(intent.getBooleanExtra("finished", false)){
                finishedCountDown()
            }
        }
    }

    private fun updateGUI(time: Long) {
        val time = getSeparatedTime(time);

        println(time)

        hour = time.hours.padStart(2,'0')
        minute = time.minutes.padStart(2, '0')
        seconds = time.seconds.padStart(2, '0')

        timeInMilli = getMilliSecondsFromSeparatedTime(SeparatedTime(hour, minute, seconds))

        tvHour.text = hour
        tvMin.text = minute
        tvSecs.text = seconds
    }

    private fun finishedCountDown() {
        state = TickStates.STOP
        setupState()
    }

    companion object {
        val POMADORA_TIME = "POMADORA_TIME"
        val SHORT_TIME = "SHORT_TIME"
        val LONG_TIME = "LONG_TIME"
        val ITERATIONS = "ITERATIONS"
    }

}