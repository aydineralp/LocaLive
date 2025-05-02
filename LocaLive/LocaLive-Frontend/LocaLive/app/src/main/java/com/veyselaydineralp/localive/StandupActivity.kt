package com.veyselaydineralp.localive

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StandupActivity : AppCompatActivity() {

    private var selectedStandupType1: String = ""
    private var selectedStandupType2: String = ""
    private var selectedStandupType3: String = ""
    private var rating1: Int = 0
    private var rating2: Int = 0
    private var rating3: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_standup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val standUpTurleri = listOf("Kara Mizah", "Gözlemsel Komedi", "Mavi Komedi")

        setupSpinner(findViewById(R.id.spinner_standup_turleri1)) { selectedStandupType1 = it }
        setupSpinner(findViewById(R.id.spinner_standup_turleri2)) { selectedStandupType2 = it }
        setupSpinner(findViewById(R.id.spinner_standup_turleri3)) { selectedStandupType3 = it }

        setupRatingBar(findViewById(R.id.ratingBar))  { rating1 = it; saveSelectionsToLocal() }
        setupRatingBar(findViewById(R.id.ratingBar2)) { rating2 = it; saveSelectionsToLocal() }
        setupRatingBar(findViewById(R.id.ratingBar3)) { rating3 = it; saveSelectionsToLocal() }
    }

    private fun setupSpinner(spinner: Spinner, callback: (String) -> Unit) {
        val standUpTurleri = listOf("Kara Mizah", "Gözlemsel Komedi", "Mavi Komedi")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            standUpTurleri
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = standUpTurleri[position]
                callback(selected)
                saveSelectionsToLocal()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRatingBar(ratingBar: RatingBar, callback: (Int) -> Unit) {
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                callback(rating.toInt())
            }
        }
    }


    private fun saveSelectionsToLocal() {
        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        editor.putInt("standupRating1", rating1)
        editor.putInt("standupRating2", rating2)
        editor.putInt("standupRating3", rating3)

        editor.putString("standupType1", selectedStandupType1)
        editor.putString("standupType2", selectedStandupType2)
        editor.putString("standupType3", selectedStandupType3)

        editor.apply()
    }

    private fun buildPreferencesString(): String {
        val preferencesList = mutableListOf<String>()

        if (selectedStandupType1.isNotEmpty()) {
            preferencesList.add(selectedStandupType1)
        }
        if (selectedStandupType2.isNotEmpty()) {
            preferencesList.add(selectedStandupType2)
        }
        if (selectedStandupType3.isNotEmpty()) {
            preferencesList.add(selectedStandupType3)
        }

        return preferencesList.joinToString(", ")
    }

    fun backToFirstChoice(view: View) {
        val intent = Intent(this, activity_firstChoice::class.java)
        startActivity(intent)
    }

    fun goToRotamap(view: View) {
        val intent = Intent(this, activity_rotamap::class.java)
        startActivity(intent)
    }
}
