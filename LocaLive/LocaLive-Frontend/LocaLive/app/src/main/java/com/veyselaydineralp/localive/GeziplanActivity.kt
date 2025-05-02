package com.veyselaydineralp.localive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.veyselaydineralp.localive.model.Event
import com.veyselaydineralp.localive.network.ApiService
import com.veyselaydineralp.localive.network.ApiClient
import org.w3c.dom.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GeziplanActivity : AppCompatActivity() {
    private var userId: Int = -1
    private lateinit var webView: WebView

    private lateinit var cardViewEvent1: CardView
    private lateinit var cardViewEvent2: CardView
    private lateinit var cardViewEvent3: CardView
    private lateinit var tvEventName1: TextView
    private lateinit var tvEventDate1: TextView
    private lateinit var tvEventLocation1: TextView
    private lateinit var tvEventLink1: TextView
    private lateinit var tvTripComment1: TextView
    private lateinit var tvEventName2: TextView
    private lateinit var tvEventDate2: TextView
    private lateinit var tvEventLocation2: TextView
    private lateinit var tvEventLink2: TextView
    private lateinit var tvTripComment2: TextView
    private lateinit var tvEventName3: TextView
    private lateinit var tvEventDate3: TextView
    private lateinit var tvEventLocation3: TextView
    private lateinit var tvEventLink3: TextView
    private lateinit var tvTripComment3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_geziplan)

        val sharedPrefs = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        userId = sharedPrefs.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Kullanıcı ID bulunamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        cardViewEvent1 = findViewById(R.id.cardViewEvent1)
        tvEventName1 = findViewById(R.id.tvEventName1)
        tvEventDate1 = findViewById(R.id.tvEventDate1)
        tvEventLocation1 = findViewById(R.id.tvEventLocation1)
        tvEventLink1 = findViewById(R.id.tvEventLink1)
        tvTripComment1 = findViewById(R.id.tvTripComment1)



        cardViewEvent2 = findViewById(R.id.cardViewEvent2)
        tvEventName2 = findViewById(R.id.tvEventName2)
        tvEventDate2 = findViewById(R.id.tvEventDate2)
        tvEventLocation2 = findViewById(R.id.tvEventLocation2)
        tvEventLink2 = findViewById(R.id.tvEventLink2)
        tvTripComment2 = findViewById(R.id.tvTripComment2)


        cardViewEvent3 = findViewById(R.id.cardViewEvent3)
        tvEventName3 = findViewById(R.id.tvEventName3)
        tvEventDate3 = findViewById(R.id.tvEventDate3)
        tvEventLocation3 = findViewById(R.id.tvEventLocation3)
        tvEventLink3 = findViewById(R.id.tvEventLink3)
        tvTripComment3 = findViewById(R.id.tvTripComment3)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true




        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("MAP_LOG", "map.html Yüklendi. Şimdi API'den verileri çekelim...")

                fetchTrips()
            }
        }

        webView.loadUrl("file:///android_asset/map.html")



    }



    private fun fetchTrips() {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.getTripsByUserId(userId)

        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!

                    if (events.isNotEmpty()) {
                        updateCardView(events[0], tvEventName1, tvEventDate1, tvEventLocation1, tvEventLink1, tvTripComment1)
                    }

                    if (events.size > 1) {
                        updateCardView(events[1], tvEventName2, tvEventDate2, tvEventLocation2, tvEventLink2, tvTripComment2)
                    }

                    if (events.size > 2) {
                        updateCardView(events[2], tvEventName3, tvEventDate3, tvEventLocation3, tvEventLink3, tvTripComment3)
                    }

                    addMarkersToMap(events)
                } else {
                    Toast.makeText(this@GeziplanActivity, "Veri alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(this@GeziplanActivity, "Hata: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCardView(
        event: Event,
        tvEventName: TextView,
        tvEventDate: TextView,
        tvEventLocation: TextView,
        tvEventLink: TextView,
        tvTripComment: TextView
    ) {
        tvEventName.text = event.eventName
        tvEventDate.text = formatDate(event.eventDate)
        tvEventLocation.text = event.eventDescription
        tvEventLink.text = "Bilet Al"
        tvTripComment.text= event.TripComment
        tvEventLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.tripUrl))
            startActivity(intent)
        }
    }

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }


    private fun addMarkersToMap(events: List<Event>) {
        for (event in events) {
            val jsCode = "javascript:addMarker(${event.latitude}, ${event.longitude}, '${event.eventName}', ${event.requestGroupId})"
            webView.evaluateJavascript(jsCode, null)
        }

        val jsCodeFit = "javascript:fitMarkers()"
        webView.evaluateJavascript(jsCodeFit, null)
    }




}
