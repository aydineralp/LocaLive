package com.veyselaydineralp.localive

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.veyselaydineralp.localive.model.RegisterRequest
import com.veyselaydineralp.localive.network.ApiClient
import com.veyselaydineralp.localive.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val UserId = Int
        val isimInput = findViewById<EditText>(R.id.isimText)
        val epostaInput = findViewById<EditText>(R.id.epostaText)
        val sifreInput = findViewById<EditText>(R.id.sifreText)
        val soyisimInput = findViewById<EditText>(R.id.soyisimText)
        val dogumTarihiInput = findViewById<EditText>(R.id.dogumText)
        val kayitOlButton = findViewById<Button>(R.id.kayitOl)
        val preferences = String
        val EventRange = Int

        kayitOlButton.setOnClickListener {
            val Id = UserId
            val isim = isimInput.text.toString()
            val eposta = epostaInput.text.toString()
            val sifre = sifreInput.text.toString()
            val soyisim = soyisimInput.text.toString()
            val dogumTarihiRaw = dogumTarihiInput.text.toString()
            val Preferences = preferences
            val eventRange = EventRange

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dogumTarihi = try {
                val parsedDate = dateFormat.parse(dogumTarihiRaw)
                dateFormat.format(parsedDate)
            } catch (e: Exception) {
                null
            }

            if (isim.isEmpty() || eposta.isEmpty() || sifre.isEmpty() || soyisim.isEmpty() || dogumTarihi == null) {
                Toast.makeText(this, "Lütfen tüm alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiService = ApiClient.instance.create(ApiService::class.java)
            val registerRequest = RegisterRequest(
                userId = 0,
                userName = isim,
                userEmail = eposta,
                userPassword = sifre,
                userSurname = soyisim,
                userDateOfBirth = dogumTarihi,
                preferences = "string",
                EventRange = "string"
            )

            apiService.AddUser(registerRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ThirdActivity, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ThirdActivity, FourthActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ThirdActivity, "Kayıt başarısız: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ThirdActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun returnSecond2(view: View) {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

    fun backGiris(view: View) {
        val intent = Intent(this, FourthActivity::class.java)
        startActivity(intent)
    }
}
