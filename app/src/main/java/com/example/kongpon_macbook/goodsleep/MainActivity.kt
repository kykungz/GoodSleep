package com.example.kongpon_macbook.goodsleep

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val PREF_NAME = "good_sleep_pref"
        val TIME_JSON_KEY = "time_json"
        val TYPE_JSON_KEY = "type_json"
    }

    private lateinit var time: String
    private lateinit var type: String

    private fun configureHome(type: String, time: String) {
        when (type) {
            "" -> textView.text = "You have no timer!"
            RecommendActivity.WAKE -> {
                textView.text = "You must go to bed at ${time}\n\nIt takes the average human about 15 minutes to fall asleep.\n"
                textView.setTextColor(Color.BLACK)
                home.setBackgroundResource(R.drawable.town_day)
            }
            RecommendActivity.SLEEP -> {
                textView.text = "You must wake up at ${time}.\n\nIf you wake up at this time, you'll rise in between 90-minute sleep cycles. A good night's sleep consists of 5-6 complete sleep cycles."
                textView.setTextColor(Color.WHITE)
                home.setBackgroundResource(R.drawable.town_night)
            }
        }
        if (time.equals("")) {
            textView.text = "You have no timer!"
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        type = pref.getString(TYPE_JSON_KEY,"")
        time = pref.getString(TIME_JSON_KEY,"")
        configureHome(type, time)

        speedDial.addActionItem(SpeedDialActionItem.Builder(R.id.fab_sleep_action,R.drawable.ic_moon_24dp)
                .setLabel("If I go to bed at ...")
                .setLabelColor(Color.BLACK)
                .create()
        )
        speedDial.addActionItem(SpeedDialActionItem.Builder(R.id.fab_wake_action,R.drawable.ic_sun_24dp)
                .setLabel("If I want to get up at ...")
                .setLabelColor(Color.BLACK)
                .setFabBackgroundColor(Color.parseColor("#FFA500"))
                .create()
        )
        speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_sleep_action -> {
                    val onTimeSet = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        Toast.makeText(this, "set ${hourOfDay} ${minute}", Toast.LENGTH_SHORT).show()
                        println("set ${hourOfDay} ${minute}")
                        val intent = Intent(this, RecommendActivity::class.java)
                        intent.putExtra(RecommendActivity.TYPE, RecommendActivity.SLEEP)
                        intent.putExtra(RecommendActivity.HOUR, hourOfDay)
                        intent.putExtra(RecommendActivity.MINUTE, minute)
                        startActivityForResult(intent, RecommendActivity.REQUEST_CODE)
                    }
                    val timePicker = TimePickerDialog(
                            this,
                            onTimeSet,
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE),
                            false
                    )
                    timePicker.show()
                }
                R.id.fab_wake_action -> {
                    val onTimeSet = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        Toast.makeText(this, "set ${hourOfDay} ${minute}", Toast.LENGTH_SHORT).show()
                        println("set ${hourOfDay} ${minute}")
                        val intent = Intent(this, RecommendActivity::class.java)
                        intent.putExtra(RecommendActivity.TYPE, RecommendActivity.WAKE)
                        intent.putExtra(RecommendActivity.HOUR, hourOfDay)
                        intent.putExtra(RecommendActivity.MINUTE, minute)
                        startActivityForResult(intent, RecommendActivity.REQUEST_CODE)
                    }
                    val timePicker = TimePickerDialog(
                            this,
                            onTimeSet,
                            8,
                            0,
                            false
                    )
                    timePicker.show()
                }
            }
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RecommendActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            time = data.getStringExtra(RecommendActivity.TIME)
            type = data.getStringExtra(RecommendActivity.TYPE)
            configureHome(type, time)
        }
    }

    override fun onStop() {
        super.onStop()
        val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with (pref.edit()) {
            this.putString(TIME_JSON_KEY, time)
            this.putString(TYPE_JSON_KEY, type)
            this.apply()
        }
    }
}
