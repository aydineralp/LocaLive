package com.veyselaydineralp.localive

import android.os.Bundle
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

class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val userId = sharedPrefs.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Kullanıcı ID bulunamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        fetchUserDetails(userId)
    }

    private fun fetchUserDetails(userId: Int) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        apiService.getUserById(userId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val data = responseBody["data"] as? Map<String, Any>
                        val userName = data?.get("userName") as? String ?: "Ad Bilinmiyor"
                        val userSurname = data?.get("userSurname") as? String ?: "Soyad Bilinmiyor"
                        val userEmail = data?.get("userEmail") as? String ?: "E-posta Bilinmiyor"

                        findViewById<TextView>(R.id.textViewFullName).text = "$userName $userSurname"
                        findViewById<TextView>(R.id.textViewEposta).text = userEmail
                    } else {
                        Toast.makeText(this@ProfilActivity, "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfilActivity, "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, "Bağlantı Hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
