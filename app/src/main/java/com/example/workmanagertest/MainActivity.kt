package com.example.workmanagertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.*
import coil.compose.rememberImagePainter
import com.example.workmanagertest.ui.theme.WorkManagerTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setting the constraints to OneTimeWorkRequestBuilder, it will only work when the network is connected
        val downloadReq = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder(
                ).setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            ).build()

        // applies the filter after downloading
        val applyFilter = OneTimeWorkRequestBuilder<ColorFilterWorker>().build()

        //WorkManager instance
        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            WorkManagerTestTheme {

                // work Info that will listen to download
                val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState()
                    .value

                //download info of whether the download is started or not
                val downloadInfo = remember(workInfos) {
                    workInfos?.find { it.id == downloadReq.id }
                }

                // color filter info
                val colorFilterInfo = remember(workInfos) {
                    workInfos?.find { it.id == applyFilter.id }
                }

                //download info
                val imageUri by derivedStateOf {
                    val downloadUrl =
                        downloadInfo?.outputData?.getString(WorkerParams.IMAGE_URI)?.toUri()
                    val colorFilter =
                        colorFilterInfo?.outputData?.getString(WorkerParams.FILTER_URI)?.toUri()
                    colorFilter ?: downloadUrl
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        imageUri?.let {
                            Image(
                                painter = rememberImagePainter(data = it),
                                contentDescription = "Image",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // begins the work on button click
                        Button(onClick = {
                            workManager.beginUniqueWork(
                                "download",
                                ExistingWorkPolicy.KEEP,
                                downloadReq
                            ).then(applyFilter).enqueue()
                        }, enabled = downloadInfo?.state != WorkInfo.State.RUNNING) {

                            Text("Download")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // checks the state of worker by using multiple info states
                        when (downloadInfo?.state) {
                            WorkInfo.State.RUNNING -> {
                                Text("Downloading (Task Running)...")
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                Text("Downloaded Successfully")

                            }
                            WorkInfo.State.FAILED -> {
                                Text("Download failed")
                            }
                            WorkInfo.State.ENQUEUED -> {
                                Text("Download Enqueued")
                            }
                            WorkInfo.State.BLOCKED -> {
                                Text("Download Blocked")
                            }
                            WorkInfo.State.CANCELLED -> {
                                Text("Download Cancel")
                            }


                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // checks wether the filter is applied or not by using the color filter info
                        when (colorFilterInfo?.state) {
                            WorkInfo.State.RUNNING -> {
                                Text("Downloading (Task Running)...")
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                Text("Downloaded Successfully")

                            }
                            WorkInfo.State.FAILED -> {
                                Text("Download failed")
                            }
                            WorkInfo.State.ENQUEUED -> {
                                Text("Download Enqueued")
                            }
                            WorkInfo.State.BLOCKED -> {
                                Text("Download Blocked")
                            }
                            WorkInfo.State.CANCELLED -> {
                                Text("Download Cancel")
                            }

                        }


                    }

                }
            }
        }
    }
}