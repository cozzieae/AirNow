package edu.appstate.cs.moments

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import okhttp3.internal.wait
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream



class ScreenshotService : Service() {
    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null
    private var msg: Message? = null
    private var webview: WebView? = null
    var filename: String = ""
    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            webview = WebView(this@ScreenshotService)

            //without this toast message, screenshot will be blank, dont ask me why...
            Toast.makeText(this@ScreenshotService, "Taking screenshot...", Toast.LENGTH_SHORT)
                .show()


            // This is the important code :)
            webview!!.isDrawingCacheEnabled = true

            //width x height of your webview and the resulting screenshot
            webview!!.measure(600, 400)
            webview!!.layout(0, 0, 600, 400)
            webview!!.loadUrl("https://fire.airnow.gov/")
            webview!!.webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {

                }

                override fun onPageFinished(view: WebView, url: String) {
                    takeScreenshotTask().execute()
                    stopSelf()
                }
            }
        }
    }

    private inner class takeScreenshotTask :
        AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(p1: Array<Void?>): Void? {

            //allow the webview to render
            synchronized(this) {
                try {
                    wait()
                } catch (e: InterruptedException) {
                }
            }

            //here I save the bitmap to file
            val b = webview!!.drawingCache
            val file = File(filename)
            val out: OutputStream
            try {
                out = BufferedOutputStream(FileOutputStream(file))
                b.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.close()
            } catch (e: IOException) {
                Log.e(
                    "ScreenshotService",
                    "IOException while trying to save thumbnail"
                )
                e.printStackTrace()
            }
            Toast.makeText(this@ScreenshotService, "Screenshot taken", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    //service related stuff below, its probably easyer to use intentService...
    override fun onCreate() {
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.looper
        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        msg = mServiceHandler!!.obtainMessage()
        msg!!.arg1 = startId
        mServiceHandler!!.sendMessage(msg!!)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {}
}