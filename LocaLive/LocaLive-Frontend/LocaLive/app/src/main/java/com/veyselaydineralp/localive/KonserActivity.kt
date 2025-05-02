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

class KonserActivity : AppCompatActivity() {

    private var selectedMusicType1: String = ""
    private var selectedMusicType2: String = ""
    private var selectedMusicType3: String = ""
    private var rating1: Int = 0
    private var rating2: Int = 0
    private var rating3: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_konser)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val musicTypes = listOf("Rock", "Pop", "Rap", "Classic")
        val spinner1: Spinner = findViewById(R.id.spinner_music_types)
        val spinner2: Spinner = findViewById(R.id.spinner_music_types2)
        val spinner3: Spinner = findViewById(R.id.spinner_music_types3)

        fun setupSpinner(spinner: Spinner, callback: (String) -> Unit) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                musicTypes
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.setOnItemSelectedListener(object :
                android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    callback(musicTypes[position])
                    saveSelectionsToLocal()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            })
        }

        setupSpinner(spinner1) { selectedMusicType1 = it }
        setupSpinner(spinner2) { selectedMusicType2 = it }
        setupSpinner(spinner3) { selectedMusicType3 = it }

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
        val maxRatedType = getMaxRatedMusicType()

        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("konserSelections", maxRatedType) // Sadece türü (Rock, Pop, vb.) sakla
            .apply()
    }


    private fun getMaxRatedMusicType(): String {
        val ratingList = listOf(rating1, rating2, rating3)
        val typeList   = listOf(selectedMusicType1, selectedMusicType2, selectedMusicType3)

        val maxRating = ratingList.maxOrNull() ?: 0
        if (maxRating == 0) {
            return ""
        }
        val maxIndex = ratingList.indexOf(maxRating)
        return typeList[maxIndex]
    }

    fun backfirstChoice(view: View) {
        val intent = Intent(this, activity_firstChoice::class.java)
        startActivity(intent)
    }

    fun goRotamap(view: View) {
        val intent = Intent(this, activity_rotamap::class.java)
        startActivity(intent)
    }
}