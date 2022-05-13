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
        val downloadReq = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder(
                ).setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            ).build()

        val applyFilter = OneTimeWorkRequestBuilder<ColorFilterWorker>().build()

        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            WorkManagerTestTheme {

                val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState()
                    .value
                val downloadInfo = remember(workInfos) {
                    workInfos?.find { it.id == downloadReq.id }

                }

                val colorFilterInfo = remember(workInfos) {
                    workInfos?.find { it.id == applyFilter.id }
                }

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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WorkManagerTestTheme {
        Greeting("Android")
    }
}