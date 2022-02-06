package com.example.mypomadora.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.mypomadora.R
import com.example.mypomadora.Utils.getMilliSecondsFromSeparatedTime
import com.example.mypomadora.models.CallBackListener
import com.example.mypomadora.models.SeparatedTime
import com.example.mypomadora.models.TimeInputTypes
import com.example.mypomadora.models.TimeSelected
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.time_entry_dialog.*

class TimeInputBottomSheet(val listener: CallBackListener<TimeSelected?>): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//      Here you set the view you want to inflate.
        return inflater.inflate(R.layout.time_entry_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//      Here you set all the actions to be performed
        super.onViewCreated(view, savedInstanceState)

        proceedButton.setOnClickListener {
            val hours = tvHour.text.toString()
            val minutes = tvMin.text.toString()
            val secs = tvSec.text.toString()
            val pomadoraTime = getMilliSecondsFromSeparatedTime(SeparatedTime(hours, minutes, secs))
//          get short break value

            val shortHours = tvHourShortBreak.text.toString()
            val shortMins = tvMinShortBreak.text.toString()
            val shortSecs = tvSecShortBreak.text.toString()
            val shortBreakTime = getMilliSecondsFromSeparatedTime(SeparatedTime(shortHours, shortMins, shortSecs))


//          Get long break value

            val longHours = tvHourLongBreak.text.toString()
            val longMins = tvHourLongBreak.text.toString()
            val longSec = tvHourLongBreak.text.toString()
            val longBreakTime = getMilliSecondsFromSeparatedTime(SeparatedTime(longHours, longMins, longSec))

//            get count
            val iterations = tvIterations.text.toString()

            listener.onSelect(TimeSelected(pomadoraTime, shortBreakTime, longBreakTime, iterations.toInt()))
            dismiss()
        }

        cancelButton.setOnClickListener {
            println("on point")
            listener.onSelect(null)
            dismiss()
        }

        tvHour.addTextChangedListener {
            if(it.toString().toInt() > 60) tvHour.setText("00")
            if(it.toString().length == 2) focusNext(TimeInputTypes.HOUR)
        }

        tvMin.addTextChangedListener {
            if(it.toString().toInt() > 60) tvMin.setText("00")
            if(it.toString().length == 2) focusNext(TimeInputTypes.MINUTE)
        }

        tvSec.addTextChangedListener {
            if(it.toString().toInt() > 60) tvSec.setText("00")
        }
    }

    private fun focusNext(current: TimeInputTypes) {
        when(current) {
            TimeInputTypes.HOUR -> {
                tvMin.requestFocus()
            }
            TimeInputTypes.MINUTE -> {
                tvSec.requestFocus()
            }
            else -> {

            }
        }
    }

    companion object {
        const val TAG = "TimeInputBottomSheet"
    }
}