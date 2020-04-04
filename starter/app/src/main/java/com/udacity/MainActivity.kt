package com.udacity

import android.animation.TimeAnimator
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), TimeAnimator.TimeListener {

    private var downloadID: Long = 0

    // hold an instance to the Notifiation Manager
    private lateinit var notificationManager: NotificationManager

    // hold an instance to the Pending Intent
    private lateinit var pendingIntent: PendingIntent

    private lateinit var action: NotificationCompat.Action

    private lateinit var mClipDrawable: ClipDrawable
    private lateinit var mAnimator: TimeAnimator
    private var mCurrentLevel: Int = 0
    private lateinit var textLabel: TextView


    // to do onCreate lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // register a BroadcastReceiver to be run in the main thread
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        /**
         * Look at Button state use with custom button and drawable background
         */
        textLabel = findViewById<View>(R.id.labelTextView) as TextView
        textLabel.text = "Click to Download"

        /**
         *  Refactor as Animation Setup
         */
        val layerDrawable = custom_button.background as LayerDrawable
        mClipDrawable = layerDrawable.findDrawableByLayerId(R.id.clip_drawable) as ClipDrawable

        mAnimator = TimeAnimator()
        mAnimator.setTimeListener(this)


        // perform download when on custom.button is clicked
        custom_button.setOnClickListener {
            if (!mAnimator.isRunning){
                textLabel.text = "Downloading"
                download()
                mCurrentLevel = 0
                mAnimator.start()
            }

        }
    }

    override fun onTimeUpdate(animation: TimeAnimator?, totalTime: Long, deltaTime: Long) {

        mClipDrawable.setLevel(mCurrentLevel)

        if (mCurrentLevel >= MAX_LEVEL){
            mAnimator.cancel()
            textLabel.text = "Complete"
        }else{
            mCurrentLevel = Math.min(MAX_LEVEL, mCurrentLevel + LEVEL_INCREMENT)
        }
    }


    //    private fun setAnimation() {
//
//        var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f,-50f)
//
//        valueAnimator.addUpdateListener {
//
//            val value = it.animatedValue as Float
//            custom_button.translationY = value
//
//        }
//
//        valueAnimator.interpolator = LinearInterpolator()
//        valueAnimator.duration = 10
//        valueAnimator.start()
//    }


    private val receiver = object : BroadcastReceiver() {

        //Upon receiving the Broadcast message assign to the receiver object
        override fun onReceive(context: Context?, intent: Intent?) {

            // create id for the broadcast message of the download message
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {

        // Use DownloadManager to perform a Uri download and send a notification+description upon completing
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        // get the downloadManger DOWNLOAD_SERVICE
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // put the request into the downloadManager's queue
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    // Kotlin "static" singleton class object
    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val LEVEL_INCREMENT = 400
        private const val MAX_LEVEL = 10000
    }

}
