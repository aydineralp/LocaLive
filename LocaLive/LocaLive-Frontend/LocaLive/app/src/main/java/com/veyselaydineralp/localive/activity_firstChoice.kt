package com.veyselaydineralp.localive

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.veyselaydineralp.localive.network.ApiClient
import com.veyselaydineralp.localive.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_firstChoice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first_choice)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val userName = sharedPrefs.getString("userName", "Misafir")

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView2)
        welcomeTextView.text = "Merhaba $userName!"
    }

    fun goKonser(view: View) {
        val intent = Intent(this, KonserActivity::class.java)
        startActivity(intent)
    }

    fun goTiyatro(view: View) {
        val intent = Intent(this, TiyatroActivity::class.java)
        startActivity(intent)
    }

    fun goStandup(view: View) {
        val intent = Intent(this, StandupActivity::class.java)
        startActivity(intent)
    }

    fun goAnamenu(view: View) {
        val intent = Intent(this, AnamenuActivity::class.java)
        startActivity(intent)
    }




    fun buttonKaydet(view: View) {
        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)


        val konserSelections   = sharedPrefs.getString("konserSelections",   "") ?: ""

        val tiyatroRating1 = sharedPrefs.getInt("tiyatroRating1", 0)
        val tiyatroRating2 = sharedPrefs.getInt("tiyatroRating2", 0)
        val tiyatroRating3 = sharedPrefs.getInt("tiyatroRating3", 0)

        val tiyatroSelections = if (tiyatroRating1 > 0 || tiyatroRating2 > 0 || tiyatroRating3 > 0) {
            "theatre"
        } else {
            ""
        }



        val standupRating1 = sharedPrefs.getInt("standupRating1", 0)
        val standupRating2 = sharedPrefs.getInt("standupRating2", 0)
        val standupRating3 = sharedPrefs.getInt("standupRating3", 0)

        val standupSelections = if (standupRating1 > 0 || standupRating2 > 0 || standupRating3 > 0) {
            "stand-up"
        } else {
            ""
        }

        val allSelections = listOf(konserSelections, tiyatroSelections, standupSelections)
            .filter { it.isNotEmpty() }
            .joinToString(", ")

        if (allSelections.isEmpty()) {
            Toast.makeText(this, "Hiçbir seçim yapılmadı!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sharedPrefs.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Kullanıcı ID bulunamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.instance.create(ApiService::class.java)
        apiService.updatePreferences(userId, allSelections).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                   Toast.makeText(
                        this@activity_firstChoice,
                        "Tüm tercihler kaydedildi",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@activity_firstChoice,
                        "Kaydetme başarısız!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@activity_firstChoice,
                    "Hata: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
