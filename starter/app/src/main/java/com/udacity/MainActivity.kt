package com.udacity

import android.animation.TimeAnimator
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), TimeAnimator.TimeListener {

    private var downloadID: Long = 0

    // hold an instance to the Notifiation Manager
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationChannel: NotificationChannel
    private val channelId = "i.apps.notifications"
    private val description = "Download notification"

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

        setupNotification()

        /**
         * Look at Button state use with custom button and drawable background
         */
        textLabel = findViewById<View>(R.id.labelTextView) as TextView

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

    /**
     * Set notification for download when completed
     */
    private fun setupNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, DetailActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        action = NotificationCompat.Action.Builder(android.R.drawable.ic_dialog_info, getString(R.string.notification_button_open), pendingIntent).build()

        // checking if android version is greater than API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.notifiction_title_complete))
                .setContentText(getString(R.string.notification_content_complete))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .addAction(action)
                .setAutoCancel(true)

        }else{

            builder = NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notifiction_title_complete))
                .setContentText(getString(R.string.notification_content_complete))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .addAction(action)
                .setAutoCancel(true)

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

            if (downloadID==id){
                notificationManager.notify(1234, builder.build())
            }
        }
    }

    private fun download() {

        Toast.makeText(this, getString(R.string.download_in_progress), Toast.LENGTH_SHORT).show()

        // Use DownloadManager to perform a Uri download and send a notification+description upon completing
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        // get the downloadManger DOWNLOAD_SERVICE
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // put the request into the downloadManager's queue
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
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
