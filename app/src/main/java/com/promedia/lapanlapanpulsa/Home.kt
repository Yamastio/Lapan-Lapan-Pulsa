package com.promedia.lapanlapanpulsa

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.promedia.lapanlapanpulsa.ui.theme.LapanColor


@Composable
fun Home(onWebViewCreated: (WebView?) -> Unit) {
    val url = "https://lapanlapan.pusatserver.id/"

    var backEnabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var webView: WebView? = null

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
                            val requestUrl =
                                request.url.toString() // Mengganti nama variabel 'url' menjadi 'requestUrl'

                            // Handler untuk WhatsApp
                            if (requestUrl.startsWith("https://wa.me/") || requestUrl.startsWith("whatsapp://")) {
                                val whatsappIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                whatsappIntent.setPackage("com.whatsapp")
                                return try {
                                    view.context.startActivity(whatsappIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    // WhatsApp tidak terpasang, arahkan ke Play Store
                                    Toast.makeText(view.context, "Silakan Install WA terlebih dahulu", Toast.LENGTH_SHORT).show()
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

                            // Handler untuk download aplikasi (contoh: Play Store atau URL APK)
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
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                telegramIntent.setPackage("org.telegram.messenger")
                                return try {
                                    view.context.startActivity(telegramIntent)
                                    true
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(view.context, "Silakan Install Telegram terlebih dahulu", Toast.LENGTH_SHORT).show()

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
                    }
                    settings.javaScriptEnabled = true

                    loadUrl(url)
                    webView = this
                    onWebViewCreated(this)
                }
            },
            update = {
                webView = it
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = LapanColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    BackHandler(enabled = backEnabled) {
        webView?.goBack()
    }
}