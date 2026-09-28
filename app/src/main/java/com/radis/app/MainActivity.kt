package com.radis.app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var swipe: SwipeRefreshLayout
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserCode = 1001
    private val homeUrl = "https://radis-co.com/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        swipe = findViewById(R.id.swipe)

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                return handleUri(uri)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipe.isRefreshing = true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                swipe.isRefreshing = false
                CookieManager.getInstance().flush()
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    swipe.isRefreshing = false
                    showErrorPage()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView?, callback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                return try {
                    startActivityForResult(fileChooserParams?.createIntent(), fileChooserCode)
                    true
                } catch (_: ActivityNotFoundException) {
                    filePathCallback = null
                    false
                }
            }
        }

        webView.setDownloadListener(DownloadListener { url, _, _, _, _ -> openExternal(Uri.parse(url)) })
        swipe.setOnRefreshListener {
            webView.clearCache(false)
            webView.reload()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })

        if (savedInstanceState == null) {
            webView.clearCache(false)
            webView.loadUrl(homeUrl)
        } else webView.restoreState(savedInstanceState)
    }

    private fun showErrorPage() {
        val html = """
            <!doctype html><html dir="rtl"><head>
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <style>body{font-family:sans-serif;background:#fff;color:#172026;display:flex;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center}.box{padding:28px;max-width:360px}h2{margin:0 0 10px}p{line-height:1.8;color:#5b6570}button{border:0;border-radius:12px;padding:13px 24px;background:#13b8b0;color:#fff;font-size:16px;font-weight:700}</style>
            </head><body><div class="box"><h2>اتصال برقرار نشد</h2><p>اینترنت را بررسی کنید و دوباره تلاش کنید.</p><button onclick="location.href='$homeUrl'">تلاش مجدد</button></div></body></html>
        """.trimIndent()
        webView.loadDataWithBaseURL(homeUrl, html, "text/html", "UTF-8", null)
    }

    private fun handleUri(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase() ?: return false
        val host = uri.host?.lowercase() ?: ""
        if (scheme == "http" || scheme == "https") {
            return if (host == "radis-co.com" || host.endsWith(".radis-co.com")) false
            else { openExternal(uri); true }
        }
        if (scheme in listOf("tel", "mailto", "sms", "whatsapp", "intent")) {
            openExternal(uri)
            return true
        }
        return false
    }

    private fun openExternal(uri: Uri) {
        try { startActivity(Intent(Intent.ACTION_VIEW, uri)) } catch (_: Exception) {}
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileChooserCode) {
            filePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            filePathCallback = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }
}
