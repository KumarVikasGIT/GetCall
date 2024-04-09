package com.vikas.getcall

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var token: String? = null
    private val db = FirebaseDatabase.getInstance()
    private var uid: String? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNotification()

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        uid = sharedPrefs.getString("userID", null)

        if (uid == null) {
            uid = (0..1000).random().toString()
            sharedPrefs.edit().putString("userID", uid)
                .putString("fcmKey", token).apply()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result
            Log.d(TAG, token!!)

            val user = FirebaseUsers(fcmID = token!!, uid = uid!!)
            db.getReference(uid!!).setValue(user)
        })

        val btn = findViewById<Button>(R.id.btnSendCall)
        btn.setOnClickListener {

            db.reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapshot ->
                        val message = childSnapshot.getValue(FirebaseUsers::class.java)
                        Log.d(TAG, "onDataChange: $message")
//                        messages.add(message!!)

                        if (message!!.uid != uid){
                            Log.d(TAG, "onCreate: $uid")
                            sendNotification(message.fcmID)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

//            getUser().forEach {
//                if (it.uid != uid){
//                    Log.d(TAG, "onCreate: $uid")
//                    sendNotification(it.fcmID)
//                }
//            }
        }

    }

    private fun getUser(): MutableList<FirebaseUsers> {
        val messages = mutableListOf<FirebaseUsers>()
        db.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val message = childSnapshot.getValue(FirebaseUsers::class.java)
                    Log.d(TAG, "onDataChange: $message")
                    messages.add(message!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        Log.d(TAG, "getUser: $messages")
        return messages
    }

    private fun sendNotification(userID: String) {
        val json = JSONObject().apply {
//            put("to", "/topics/all") // Send to all devices subscribed to the topic "all"
            put("priority", "high") // Set priority to high for immediate delivery
            put("to", userID)
            put("data", JSONObject().apply {
                put("title", "Notification Title")
                put("message", "Notification Message")
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(RequestBody.create(mediaType, json.toString()))
            .header(
                "Authorization",
                "key=AAAAK0vCTGo:APA91bGO2Pd5vLyLUgeqvcsO7ytxiwcEiEj2yLmuj1HB6CSergbmA6lKjVSitPCVJQpwSLPYRmA_yphzO7VJnv7G8XdiyO2s_6jI7Kc_fhlTDjBOJ0sHg9AZV6XJSTtoVg_z7BttdD2L" // Replace YOUR_SERVER_KEY with your actual FCM server key
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse: ${response.code}")
                Log.d(TAG, "onResponse: ${response.body?.string()}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotification(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                11
            )
        } else {
            // Permission has already been granted
            // Proceed with your logic
        }

    }
}