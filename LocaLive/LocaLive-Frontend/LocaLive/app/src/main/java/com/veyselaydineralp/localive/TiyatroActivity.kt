package com.veyselaydineralp.localive

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TiyatroActivity : AppCompatActivity() {

    private var selectedTheatreType1: String = ""
    private var selectedTheatreType2: String = ""
    private var selectedTheatreType3: String = ""
    private var rating1: Int = 0
    private var rating2: Int = 0
    private var rating3: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tiyatro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val theatreTypes = listOf("Drama", "Komedi", "Müzikal")

        fun setupSpinner(spinner: Spinner, callback: (String) -> Unit) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                theatreTypes
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    callback(theatreTypes[position])
                    saveSelectionsToLocal()
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            })
        }

        val spinner1: Spinner = findViewById(R.id.spinner_tiyatro_turleri1)
        val spinner2: Spinner = findViewById(R.id.spinner_tiyatro_turleri2)
        val spinner3: Spinner = findViewById(R.id.spinner_tiyatro_turleri3)

        setupSpinner(spinner1) { selectedTheatreType1 = it }
        setupSpinner(spinner2) { selectedTheatreType2 = it }
        setupSpinner(spinner3) { selectedTheatreType3 = it }

        val ratingBar1 = findViewById<RatingBar>(R.id.ratingBar)
        val ratingBar2 = findViewById<RatingBar>(R.id.ratingBar2)
        val ratingBar3 = findViewById<RatingBar>(R.id.ratingBar3)

        ratingBar1.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                rating1 = rating.toInt()
                saveSelectionsToLocal()
            }
        }
        ratingBar2.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                rating2 = rating.toInt()
                saveSelectionsToLocal()
            }
        }
        ratingBar3.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                rating3 = rating.toInt()
                saveSelectionsToLocal()
            }
        }
    }


    private fun saveSelectionsToLocal() {
        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        editor.putInt("tiyatroRating1", rating1)
        editor.putInt("tiyatroRating2", rating2)
        editor.putInt("tiyatroRating3", rating3)

        editor.putString("theatreType1", selectedTheatreType1)
        editor.putString("theatreType2", selectedTheatreType2)
        editor.putString("theatreType3", selectedTheatreType3)

        editor.apply()
    }

    private fun buildPreferencesString(): String {
        val preferencesList = mutableListOf<String>()

        if (selectedTheatreType1.isNotEmpty()) {
            preferencesList.add(selectedTheatreType1)
        }
        if (selectedTheatreType2.isNotEmpty()) {
            preferencesList.add(selectedTheatreType2)
        }
        if (selectedTheatreType3.isNotEmpty()) {
            preferencesList.add(selectedTheatreType3)
        }

        return preferencesList.joinToString(", ")
    }

    fun backfirstChoice(view: View) {
        val intent = Intent(this, activity_firstChoice::class.java)
        startActivity(intent)
    }
}
