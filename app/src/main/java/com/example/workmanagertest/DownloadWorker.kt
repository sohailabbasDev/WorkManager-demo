package com.example.workmanagertest

import android.content.Context
import androidx.core.app.NotificationCompat

import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class DownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        startForegroundService()
        delay(1000L)
        val res = FileApi.instance.getImage()
        res.body()?.let { body ->
            return withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes())
                    } catch (e: IOException) {
                        return@withContext Result.failure(
                            workDataOf(WorkerParams.ERROR_MSG to e.localizedMessage)
                        )
                    }

                }
                Result.success(workDataOf(WorkerParams.IMAGE_URI to file.toURI().toString()))

            }
        }

        if (!res.isSuccessful) {
            if (res.code().toString().startsWith("5")) {
                return Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Server Error"))
            }
        }
        return Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Unknown Error"))

    }


    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in Progress")
                    .build()
            )
        )
    }
}