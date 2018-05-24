package com.example.kongpon_macbook.goodsleep

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_time_picker.*
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.MutableDateTime
import java.util.*
import org.joda.time.format.DateTimeFormat



class RecommendActivity : AppCompatActivity() {
    private val formatter = DateTimeFormat.forPattern("hh:mm a")
    private lateinit var target: DateTime

    companion object {
        val REQUEST_CODE = 123
        val TYPE = "timePicker_type"
        val WAKE = "timePicker_wake"
        val SLEEP = "timePicker_sleep"
        val HOUR = "timePicker_hour"
        val MINUTE = "timePicker_minute"
        val TIME = "timePicker_time"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_picker)
        JodaTimeAndroid.init(this)
        val type = intent.getStringExtra(TYPE)
        val temp = MutableDateTime()
        temp.hourOfDay = intent.getIntExtra(HOUR, 0)
        temp.minuteOfHour = intent.getIntExtra(MINUTE, 0)
        target = DateTime(temp)
        when (type) {
            WAKE -> time.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            SLEEP -> time.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        }
        time.text = target.toString(formatter)

        when (intent.getStringExtra(TYPE)) {
            WAKE -> configureWake()
            SLEEP -> configureSleep()
        }
    }

    private fun configureWake() {
        val buttons = listOf<Button>(time1, time2, time3, time4, time5, time6).asReversed()
        for ((index, time) in buttons.withIndex()) {
            val recommended = target.minusMinutes(15 + 90 * (index + 1))
            println(target.toString(DateTimeFormat.forPattern("hh:mm a")))
            time.text = recommended.toString(formatter)
            time.setTextColor(Color.WHITE)
            time.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            time.setOnClickListener(View.OnClickListener { v ->
                val intent = Intent()
                intent.putExtra(TIME, recommended.toString(formatter))
                intent.putExtra(TYPE, WAKE)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }
        text1.text = "If you want to wake up at"
        text2.text = "You must sleep at one of the following times:"
    }

    private fun configureSleep() {
        val buttons = listOf<Button>(time1, time2, time3, time4, time5, time6)
        for ((index, time) in buttons.withIndex()) {
            val recommended = DateTime().plusMinutes(15 + 90 * (index + 1))
            time.text = recommended.toString(formatter)
            time.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
            time.setOnClickListener(View.OnClickListener { v ->
                val intent = Intent()
                intent.putExtra(TIME, recommended.toString(formatter))
                intent.putExtra(TYPE, SLEEP)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }
        text1.text = "If you go to bed at"
        text2.text = "You should try to get up at one of the following times:"
    }
}
