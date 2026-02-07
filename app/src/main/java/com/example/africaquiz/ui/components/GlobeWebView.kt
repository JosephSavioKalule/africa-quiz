package com.example.africaquiz.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GlobeWebView(
    modifier: Modifier = Modifier,
    capitalLat: Double? = null,
    capitalLng: Double? = null
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(capitalLat, capitalLng, isLoaded) {
        if (!isLoaded) return@LaunchedEffect
        val wv = webView ?: return@LaunchedEffect

        if (capitalLat != null && capitalLng != null) {
            wv.evaluateJavascript("showCapital($capitalLat, $capitalLng)", null)
        } else {
            wv.evaluateJavascript("clearMarker()", null)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()

            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                setBackgroundColor(android.graphics.Color.parseColor("#0A1628"))

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: android.webkit.WebResourceRequest
                    ): android.webkit.WebResourceResponse? {
                        return assetLoader.shouldInterceptRequest(request.url)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoaded = true
                    }
                }

                loadUrl("https://appassets.androidplatform.net/assets/globe/index.html")
                webView = this
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }
}
