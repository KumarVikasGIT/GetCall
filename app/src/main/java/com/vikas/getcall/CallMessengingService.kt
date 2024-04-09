package com.vikas.getcall

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.annotations.NotNull
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@Suppress("DEPRECATION")
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
//class CallMessengingService : FirebaseMessagingService() {
//
////    override fun onMessageReceived(message: RemoteMessage) {
////        super.onMessageReceived(message)
////        message.notification?.let {
////            val title = it.title ?: "Notification Title"
////            val body = it.body ?: "Notification Body"
////
////            // Create an intent to launch FullScreenActivity when notification is tapped
////            val fullScreenIntent = Intent(this, CallReceiver::class.java)
////            fullScreenIntent.flags =
////                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////            val fullScreenPendingIntent =
////                PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_MUTABLE)
////
////            // Create a full-screen notification
////            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
////                .setSmallIcon(R.drawable.ic_launcher_background)
////                .setContentTitle(title)
////                .setContentText(body)
////                .setPriority(NotificationCompat.PRIORITY_HIGH)
////                .setFullScreenIntent(fullScreenPendingIntent, true)
////
////            // Show the notification
////            with(NotificationManagerCompat.from(this)) {
////                if (ActivityCompat.checkSelfPermission(
////                        baseContext,
////                        Manifest.permission.POST_NOTIFICATIONS
////                    ) != PackageManager.PERMISSION_GRANTED
////                ) {
////                    return
////                }
////                notify(0, notificationBuilder.build())
////            }
////        }
////    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        Log.d("TAG", "onMessageReceived: receved")
//
//        // Extract data from the FCM message
//        val data = remoteMessage.data
//        val title = data["title"] ?: ""
//        val message = data["message"] ?: ""
//        val intentAction = data["intentAction"] ?: ""
//
//        Log.d("TAGTAG", "onMessageReceived: $title")
//        Log.d("TAGTAG", "onMessageReceived: $message")
//        Log.d("TAGTAG", "onMessageReceived: $intentAction")
//
//        if (intentAction.isNotEmpty()) {
//            // Check if full-screen intent is allowed (consider Android version)
//            val useFullScreenIntent = canUseFullScreenIntent()
//
//            // Create the full-screen intent or a regular intent
//            val intent = if (useFullScreenIntent) {
//                createFullScreenIntent(intentAction)
//            } else {
//                createRegularIntent(intentAction)
//            }
//
//            // Build the notification with the title and message
//            val notification = buildNotification(title, message, intent)
//
//            // Send the notification
//            with(NotificationManagerCompat.from(applicationContext)) {
//                if (ActivityCompat.checkSelfPermission(
//                        applicationContext,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//                notify(1, notification)
//            }
//        }
//    }
//
//    private fun canUseFullScreenIntent(): Boolean {
//        // Implement logic to check permission based on Android version
//        // (e.g., check for USE_FULL_SCREEN_INTENT permission or if app has calling/alarm functionality on Android 14+)
//        return true // Replace with your permission check
//    }
//
//    private fun createFullScreenIntent(intentAction: String): PendingIntent {
//        val intent = Intent(this, CallReceiver::class.java)
//        intent.action = intentAction
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//    }
//
//    private fun createRegularIntent(intentAction: String): PendingIntent {
//        val intent = Intent(this, CallReceiver::class.java)
//        intent.action = intentAction
//        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//    }
//
//    private fun buildNotification(title: String, message: String, intent: PendingIntent): Notification {
//        // Implement notification builder logic with title, message, icon, etc.
//        // Set the content intent to the created pending intent
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setContentIntent(intent)
//        // ... (other notification settings)
//
//        return builder.build()
//    }
//}


class MyFirebaseMessagingService() : FirebaseMessagingService() {
    // https://stackoverflow.com/questions/12172092/check-if-activity-is-running-from-service
    private fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return componentInfo!!.packageName == myPackage
    }

    /**
     * Called when message is received. AND when the app is in foreground (see the chart in link)
     * https://firebase.google.com/docs/cloud-messaging/android/receive#handling_messages
     * system will not automatically display a notification
     * but we can manually create a notification in android
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        DebugLog.i("received notification from ecos")
        val extras = Bundle()

        // Check if message contains a data payload.
        val dataPayLoad = remoteMessage.getData()
        for (entry: Map.Entry<String, String> in dataPayLoad.entries) {
            DebugLog.i("dataPayLoad: " + entry.key + " " + entry.value)
            extras.putString(entry.key, entry.value)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            val body = remoteMessage.getNotification()!!.body
            DebugLog.i("Message Notification Body: $body")
            extras.putString("notificationPayload", body)
        }


        // start the app from the background
        // Is there a way to bring an application to foreground on push notification receive?
        // https://stackoverflow.com/questions/51393431/is-there-a-way-to-bring-an-application-to-foreground-on-push-notification-receiv


        // How to launch main Activity from Notification in a service library without hard coding Activity class
        // https://stackoverflow.com/questions/13557654/how-to-launch-main-activity-from-notification-in-a-service-library-without-hard
        // This block is for deciding what should the click action do
        val startAppIntent = Intent()
        val ctx: Context = applicationContext
        val mPackage: String = ctx.getPackageName()
        val mClass: String = ctx.getResources().getString(R.string.main_activity_name)
        // R.string.main_activity_name will be overridden by the module using this library
        startAppIntent.setComponent(ComponentName(mPackage, mClass))
        val activityClass: Class<*>
        try {
            activityClass = Class.forName("$mPackage.$mClass") // "com.aitmed.testpage.MainActivity"
        } catch (e: ClassNotFoundException) {
            DebugLog.e("activityClass is null")
            e.printStackTrace()
            return
        }

        // first check the app status
        // may be in foreground/background/ swiped away
        /*
            if in foreground, send board cast to execute noodl, easy case
            if in background or swiped away,
                - if  dataPayload == shouldOpen, open activity -> send broadcast
                - else:  display customized heads up banner
         */if (isForeground(mPackage)) {
            sendBroadcast(extras)
        } else {
//            DebugLog.i("Original Priority is " + remoteMessage.getOriginalPriority());
//            DebugLog.i("Priority is " + remoteMessage.getPriority());
            wakeApp()
            if (extras.getString("openApp", "") == "true") {
                startActivity(activityClass, extras)
            } else {
                showNotification(remoteMessage, dataPayLoad, activityClass, extras)
            }
        }


        // hard coded if openApp is true then vibrate and ringtone
        if (extras.getString("openApp", "") == "true") {
            startVibrate()
            startRingtone()
        }
    }

    fun startVibrate() {
//        val v: Vibrator = PhoneControl.getVibrator(applicationContext)
//        PhoneControl.startVibrate(v)
        Log.d("TAG", "startVibrate: Vibrate")
    }

    fun startRingtone() {
//        val player: MediaPlayer = PhoneControl.getMediaPlayer(applicationContext)
//        player.start()
        Log.d("TAG", "startRingtone: Ringtone")
    }

    // [END receive_message]
    // may be screen is off when receive notification
    private fun wakeApp() {
        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive // check if screen is on
        DebugLog.i("wakeApp$screenIsOn")
        if (!screenIsOn) {
            val wakeLockTag = packageName + "WAKELOCK"
            val wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, wakeLockTag
            )

            //acquire will turn on the display
            wakeLock.acquire()

            //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
            wakeLock.release()
        }
    }

    private fun sendBroadcast(extras: Bundle) {
        DebugLog.i("Sending broadcast after received a message")
        val intent = Intent("onNewEcosDoc")
        intent.putExtras(extras)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun startActivity(activityClass: Class<*>, extras: Bundle) {
        DebugLog.i("Starting activity...")
        val myIntent = Intent(this, activityClass)
        myIntent.setFlags(
            (
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        myIntent.setAction("android.intent.action.MAIN")
        myIntent.addCategory("android.intent.category.LAUNCHER")
        myIntent.putExtras(extras)
        this.applicationContext.startActivity(myIntent)
    }

    private fun createNotificationChannel(channelID: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Foreground Service Channel"
            val description = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // https://stackoverflow.com/questions/40181654/firebase-fcm-open-activity-and-pass-data-on-notification-click-android
    private fun showNotification(
        remoteMessage: RemoteMessage,
        dataPayLoad: Map<String, String>,
        activityClass: Class<*>,
        extras: Bundle,
    ) {
        val channelID = "NOTIFICATION_CHANNEL_ID"
        createNotificationChannel(channelID)

        // Check if message contains a notification payload.
        val title = if (dataPayLoad.containsKey("title")) dataPayLoad["title"] else "title"
        val body = if (dataPayLoad.containsKey("body")) dataPayLoad["body"] else "body"
        val notificationIntent = Intent(applicationContext, activityClass)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.putExtras(extras)
        val resultIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultIntent)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, mBuilder.build())
    }
    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(@NotNull token: String) {
        DebugLog.i("onNewToken FirebaseToken: $token")
        val context: Context = applicationContext
        val intent = Intent("FCMOnTokenReceive")
        intent.putExtra("FCMToken", token)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        DebugLog.i("onDestroy: Service is destroyed")
    }

    companion object {
        // [END on_new_token]
        /*
     * Should be called after login (ce 1030 edge),\
     *  will send the current FCM token to backend
     */
        fun sendCurrentToken(context: Context?) {
            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(OnCompleteListener<String> { task ->
                    if (!task.isSuccessful) {
                        DebugLog.e("Fetching FCM registration token failed")
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast
                    DebugLog.e("This is token   $token")
                    val intent = Intent("FCMOnTokenReceive")
                    intent.putExtra("FCMToken", token)
                    context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
                })
        }
    }
}

object DebugLog {

    fun e(string: String) {
        Log.d("TAG", "e: $string")
    }

    fun i(string: String) {
        Log.d("TAG", "e: $string")
    }
}
