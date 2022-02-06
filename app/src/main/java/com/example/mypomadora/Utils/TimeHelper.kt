package com.example.mypomadora.Utils

import com.example.mypomadora.models.SeparatedTime

fun getSeparatedTime(secondsToConvert: Long) : SeparatedTime {

    val hours = (secondsToConvert / 3600).toInt()
    val minutes = (secondsToConvert % 3600 / 60).toInt()
    val seconds = (secondsToConvert % 3600 % 60).toInt()

    return SeparatedTime(
        hours = hours.toString(),
        minutes = minutes.toString(),
        seconds = seconds.toString()
    )
}

fun getMilliSecondsFromSeparatedTime(time: SeparatedTime): Long {
    var finalTime = 0L

    finalTime += (time.hours.toLong() * 3600)

    finalTime += (time.minutes.toLong() * 60)

    finalTime += time.seconds.toLong()

    return finalTime * 1000
}