package com.veyselaydineralp.localive

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location as AndroidLocation
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.veyselaydineralp.localive.model.ApiResponse
import com.veyselaydineralp.localive.model.Location
import com.veyselaydineralp.localive.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class AnamenuActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var editTextCity: EditText
    private lateinit var buttonSubmitCity: Button
    private lateinit var buttonStartDate: Button
    private lateinit var buttonEndDate: Button
    private lateinit var textViewStartDate: TextView
    private lateinit var textViewEndDate: TextView
    private lateinit var buttonKaydet: Button // EKLENEN: Kaydet butonu

    private var userId: Int = -1 // Kullanıcı ID değişkeni
    private lateinit var apiService: ApiService

    private var startDate: String? = null
    private var endDate: String? = null

    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Kullanıcıya gösterilecek format
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anamenu)

        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        userId = sharedPrefs.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Kullanıcı ID bulunamadı!", Toast.LENGTH_LONG).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("YOUR-API-URL") // Backend API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)


        spinner = findViewById(R.id.spinner)
        editTextCity = findViewById(R.id.editTextCity)
        buttonSubmitCity = findViewById(R.id.buttonSubmitCity)
        buttonStartDate = findViewById(R.id.buttonStartDate)
        buttonEndDate = findViewById(R.id.buttonEndDate)
        textViewStartDate = findViewById(R.id.textViewStartDate)
        textViewEndDate = findViewById(R.id.textViewEndDate)
        buttonKaydet = findViewById(R.id.buttonKaydet)

        setupSpinner()

        buttonSubmitCity.setOnClickListener {
            val cityName = editTextCity.text.toString().trim()
            if (cityName.isNotEmpty()) {
                handleCitySubmission(cityName)
            } else {
                Toast.makeText(this, "Lütfen bir şehir ismi giriniz!", Toast.LENGTH_LONG).show()
            }

            hideViewsWithAnimation(editTextCity, buttonSubmitCity)
        }

        buttonStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = isoDateFormat.format(selectedDate)
                textViewStartDate.text = "Başlangıç: ${displayDateFormat.format(selectedDate)}"
            }
        }

        buttonEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = isoDateFormat.format(selectedDate)
                textViewEndDate.text = "Bitiş: ${displayDateFormat.format(selectedDate)}"
            }
        }

        buttonKaydet.setOnClickListener {
            if (startDate != null && endDate != null) {
                apiService.getUnifiedProcess(userId, startDate!!, endDate!!)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AnamenuActivity,
                                    "API isteği başarılı!",
                                    Toast.LENGTH_LONG
                                ).show()
                                goGeziplan()
                            } else {
                                Toast.makeText(
                                    this@AnamenuActivity,
                                    "API isteği başarısız: ${response.code()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(
                                this@AnamenuActivity,
                                "API isteği sırasında hata: ${t.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Lütfen tarihleri seçiniz!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSpinner() {
        val spinnerOptions = arrayOf("", "Otomatik", "Manuel")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                if (selectedOption == "Manuel") {
                    editTextCity.visibility = View.VISIBLE
                    buttonSubmitCity.visibility = View.VISIBLE
                }
                else if (selectedOption == "Otomatik") {
                    editTextCity.visibility = View.GONE
                    buttonSubmitCity.visibility = View.GONE
                    requestLocationPermissionAndFetch()
                }
                else {
                    editTextCity.visibility = View.GONE
                    buttonSubmitCity.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun requestLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchUserLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                fetchUserLocation()
            } else {
                Toast.makeText(this, "Konum izni reddedildi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: AndroidLocation? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Toast.makeText(this, "Güncel konum alındı: $latitude, $longitude", Toast.LENGTH_SHORT).show()

                    val autoLocation = Location(
                        LocationId = userId,
                        Country = "Turkey",
                        City = "",
                        Latitude = latitude,
                        Longitude = longitude,
                        isAutoDetected = true
                    )

                    apiService.getLocationById(userId).enqueue(object : Callback<ApiResponse<Location>> {
                        override fun onResponse(
                            call: Call<ApiResponse<Location>>,
                            response: Response<ApiResponse<Location>>
                        ) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                updateLocation(autoLocation)
                            } else {
                                addLocation(autoLocation)
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<Location>>, t: Throwable) {
                            Toast.makeText(this@AnamenuActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Konum bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Konum alınırken hata: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun handleCitySubmission(cityName: String) {
        val location = Location(
            LocationId = userId,
            Country = "Turkey",
            City = cityName,
            Latitude = 0.0,
            Longitude = 0.0,
            isAutoDetected = false
        )

        apiService.getLocationById(userId).enqueue(object : Callback<ApiResponse<Location>> {
            override fun onResponse(
                call: Call<ApiResponse<Location>>,
                response: Response<ApiResponse<Location>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    updateLocation(location)
                } else {
                    addLocation(location)
                }
            }

            override fun onFailure(call: Call<ApiResponse<Location>>, t: Throwable) {
                Toast.makeText(this@AnamenuActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addLocation(location: Location) {
        apiService.addLocation(location).enqueue(object : Callback<ApiResponse<Location>> {
            override fun onResponse(
                call: Call<ApiResponse<Location>>,
                response: Response<ApiResponse<Location>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AnamenuActivity, "Lokasyon eklendi!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@AnamenuActivity, "Ekleme başarısız!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Location>>, t: Throwable) {
                Toast.makeText(this@AnamenuActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateLocation(location: Location) {
        apiService.updateLocation(userId, location).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AnamenuActivity, "Lokasyon güncellendi!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@AnamenuActivity, "Güncelleme başarılı!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                Toast.makeText(this@AnamenuActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun hideViewsWithAnimation(vararg views: View) {
        for (view in views) {
            view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction { view.visibility = View.GONE }
                .start()
        }
    }

    fun goProfil(view: View) {
        val intent = Intent(this, ProfilActivity::class.java)
        startActivity(intent)
    }
    fun goGeziplan(view: View) {
        val intent = Intent(this, GeziplanActivity::class.java)
        startActivity(intent)
    }
    private fun goGeziplan() {
        val intent = Intent(this, GeziplanActivity::class.java)
        startActivity(intent)
    }
}
