package com.veyselaydineralp.localive

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_rotamap : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rotamap)

        val webView: WebView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true  // JavaScript'i aktif et
        webView.settings.domStorageEnabled = true // DOM depolamayı aktif et
        webView.settings.allowFileAccess = true // Dosya erişimini aktif et
        webView.settings.allowContentAccess = true // İçerik erişimini aktif et
        webView.settings.javaScriptCanOpenWindowsAutomatically = true // JS pencerelerini açma izni

        webView.webViewClient = WebViewClient()

        webView.loadUrl("file:///android_asset/map.html")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
