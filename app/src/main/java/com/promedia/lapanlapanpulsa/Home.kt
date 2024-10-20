package com.promedia.lapanlapanpulsa

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.promedia.lapanlapanpulsa.ui.theme.LapanColor

@Composable
fun WebViewWithErrorHandling(onWebViewCreated: (WebView?) -> Unit) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isError by remember { mutableStateOf(false) } // Untuk cek apakah terjadi error
    var isConnected by remember { mutableStateOf(isOnline(context)) } // Status koneksi
    var showErrorToast by remember { mutableStateOf(false) }

    // Cek koneksi secara real-time
    DisposableEffect(Unit) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                if (isError) {
                    isError = false
                    // Reload jika ada error dan koneksi tersedia
                    Handler(Looper.getMainLooper()).post {
                        webViewInstance?.reload()
                    }
                }
            }

            override fun onLost(network: Network) {
                isConnected = false
                isError = true // Menampilkan screen error
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        // Clean up when the composable is disposed
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    // Menampilkan ErrorScreen jika ada error
    if (isError) {
        ErrorScreen(
            onReload = {
                if (isConnected) {
                    isError = false
                    Handler(Looper.getMainLooper()).post {
                        webViewInstance?.reload() // Reload WebView di thread utama
                    }
                } else {
                    showErrorToast = true // Tampilkan toast jika belum ada koneksi
                }
            }
        )
    } else {
        Home(
            onWebViewCreated = {
                webViewInstance = it
                onWebViewCreated(it) // Pass the instance upwards
            },
            onErrorOccurred = {
                if (!isConnected) {
                    isError = true
                }
            }
        )
    }

    // Menampilkan toast jika koneksi internet belum aktif
    if (showErrorToast) {
        Toast.makeText(context, "Koneksi internet belum aktif", Toast.LENGTH_SHORT).show()
        showErrorToast = false
    }
}

@Composable
fun Home(onWebViewCreated: (WebView?) -> Unit, onErrorOccurred: () -> Unit) {
    val url = "https://lapanlapan.pusatserver.id/"
    var backEnabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var webView: WebView? = null // Deklarasikan di sini agar bisa diakses dalam scope ini

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            val requestUrl = request.url.toString()

                            // Handler untuk WhatsApp
                            if (requestUrl.startsWith("https://wa.me/") || requestUrl.startsWith("whatsapp://")) {
                                val whatsappIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl)).apply {
                                        setPackage("com.whatsapp")
                                    }
                                return try {
                                    view.context.startActivity(whatsappIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        view.context,
                                        "Silakan Install WA terlebih dahulu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    true
                                }
                            }

                            // Handler untuk SMS
                            if (requestUrl.startsWith("sms:")) {
                                val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(requestUrl))
                                return try {
                                    view.context.startActivity(smsIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    true
                                }
                            }

                            // Handler untuk download aplikasi
                            if (requestUrl.contains(".apk") || requestUrl.startsWith("https://play.google.com/") || requestUrl.startsWith(
                                    "https://drive.google.com/"
                                )
                            ) {
                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                return try {
                                    view.context.startActivity(browserIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    true
                                }
                            }

                            // Handler untuk Telegram
                            if (requestUrl.startsWith("tg:")) {
                                val telegramIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl)).apply {
                                        setPackage("org.telegram.messenger")
                                    }
                                return try {
                                    view.context.startActivity(telegramIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        view.context,
                                        "Silakan Install Telegram terlebih dahulu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    true
                                }
                            }

                            return false // Handle URL lain oleh WebView
                        }

                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            backEnabled = view.canGoBack()
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView, url: String?) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            onErrorOccurred()
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                    webView = this // Menyimpan instance webView yang baru
                    onWebViewCreated(this)
                }
            },
            update = {
                webView = it // Update instance webView yang sudah ada
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // Memberikan background putih penuh
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = LapanColor, // Warna indikator sesuai preferensi
                    trackColor = MaterialTheme.colorScheme.surfaceVariant // Warna background indikator
                )
            }
        }
    }

    BackHandler(enabled = backEnabled) {
        webView?.goBack()
    }
}

@Composable
fun ErrorScreen(onReload: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ooppss...!!!",
            fontSize = 30.sp,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = "Pastikan koneksi internet kamu berjalan dengan baik.",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Sambungkan ke WiFi atau hidupkan data seluler, kemudian tekan tombol Reload.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(70.dp))
        Button(onClick = onReload) {
            Text(text = "Reload")
        }
    }
}

// Fungsi pengecekan koneksi
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(onReload = {})
}