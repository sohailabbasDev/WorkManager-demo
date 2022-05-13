package com.example.workmanagertest

import android.content.Context
import android.graphics.*

import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ColorFilterWorker(
    private val context: Context,
    private val workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val imageFile = workParams.inputData.getString(WorkerParams.IMAGE_URI)?.toUri()?.toFile()

        delay(3000L)
        return imageFile?.let { file ->
            val bmp = BitmapFactory.decodeFile(file.absolutePath)
            val resultbmp = bmp.copy(bmp.config, true)
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(0x08FF04, 1)
            val canvas = Canvas()
            canvas.drawBitmap(resultbmp, 0f, 0f, paint)
            withContext(Dispatchers.IO) {
                val resultFile = File(context.cacheDir, "result.jpg")
                val outputStream = FileOutputStream(resultFile)
                val success = resultbmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                if (success) {
                    Result.success(
                        workDataOf(
                            WorkerParams.FILTER_URI to resultFile.toURI().toString()
                        )
                    )
                } else {
                    Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Failed to save image"))
                }
            }
        } ?: Result.failure(workDataOf(WorkerParams.ERROR_MSG to "Image not found"))
    }
}