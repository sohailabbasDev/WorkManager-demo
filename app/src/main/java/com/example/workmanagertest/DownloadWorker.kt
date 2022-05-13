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

// coroutine worker to download image
class DownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    // over ridden suspend fun that will give the result
    override suspend fun doWork(): Result {

        // calls the function fore ground service
        startForegroundService()
        delay(1000L)

        //gets the image from API
        val res = FileApi.instance.getImage()

        // now we will let the body and write the file in, i.e download the file
        res.body()?.let { body ->
            return withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        // this writes the file to location
                        stream.write(body.bytes())
                    } catch (e: IOException) {
                        return@withContext Result.failure(
                            workDataOf(WorkerParams.ERROR_MSG to e.localizedMessage)
                        )
                    }

                }
                //Success state as in this case the image is downlaoded
                Result.success(workDataOf(WorkerParams.IMAGE_URI to file.toURI().toString()))

            }
        }

        //checks whether is successful or not
        if (!res.isSuccessful) {
            if (res.code().toString().startsWith("5")) {
                return Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Server Error"))
            }
        }

        //returns the result
        return Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Unknown Error"))

    }


    // This function sets a notification builder
    private suspend fun startForegroundService() {

        // This on emakes fore ground run a=in a context of a fore ground
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