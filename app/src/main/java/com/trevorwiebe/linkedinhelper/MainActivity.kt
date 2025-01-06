package com.trevorwiebe.linkedinhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trevorwiebe.linkedinhelper.data.FetchJobs
import com.trevorwiebe.linkedinhelper.data.Job
import com.trevorwiebe.linkedinhelper.ui.theme.LinkedInHelperTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val jobList = mutableStateListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scope.launch {
            val fetchedJobs = FetchJobs().invoke()
            jobList.clear() // Clear existing items
            jobList.addAll(fetchedJobs.processJobList()) // Add new items
        }

        enableEdgeToEdge()
        setContent {
            LinkedInHelperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(jobList){
                            JobCard(job = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(
    modifier: Modifier = Modifier,
    job: Job
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = job.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = job.company)
        Text(text = job.location)
        Text(text = job.timePosted)
        Text(text = job.date)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
    }
}

private fun List<Job>.processJobList(): List<Job> {
    return this
        .filter { it.title.contains("Android", ignoreCase = true) }
        .sortedBy { it.timePosted.substringBefore(" ").toInt() }
}
