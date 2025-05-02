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
import com.veyselaydineralp.localive.model.LoginRequest
import com.veyselaydineralp.localive.model.LoginResponse
import com.veyselaydineralp.localive.network.ApiClient
import com.veyselaydineralp.localive.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FourthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fourth)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val epostaInput = findViewById<EditText>(R.id.epostaInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val userEmail = epostaInput.text.toString().trim()
            val userPassword = passwordInput.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiService = ApiClient.instance.create(ApiService::class.java)
            val loginRequest = LoginRequest(userEmail, userPassword)

            apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.success) {

                            Toast.makeText(
                                this@FourthActivity,
                                "Giriş başarılı!",

                                Toast.LENGTH_SHORT
                            ).show()

                            val userId =
                                loginResponse.data?.userId ?: -1
                            val userName = loginResponse.data?.userName ?: ""

                            val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putInt("userId", userId)
                            editor.putString("userName", userName)

                            editor.apply()

                            val intent =
                                Intent(this@FourthActivity, activity_firstChoice::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@FourthActivity,
                                "Giriş başarısız: ${loginResponse?.message ?: "Hata"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: response.message()
                        Toast.makeText(
                            this@FourthActivity,
                            "Şifreniz yanlış. Tekrardan kontrol ediniz.: $errorMsg",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@FourthActivity,
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


            fun returnSecond(view: View) {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }

            fun goFirstChoice(view: View) {
                val intent = Intent(this, activity_firstChoice::class.java)
                startActivity(intent)
            }
        }
    }
}