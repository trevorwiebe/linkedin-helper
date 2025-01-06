package com.trevorwiebe.linkedinhelper.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class FetchJobs(){

    operator fun invoke(): List<Job> {
        val jobList: MutableList<Job> = mutableListOf()
        try {
            // Build the LinkedIn job search URL
            val url = "https://www.linkedin.com/jobs/search/?currentJobId=4116831683&distance=25&f_TPR=r86400&geoId=103644278&keywords=android%20developer&origin=JOB_SEARCH_PAGE_JOB_FILTER"

            // Fetch the HTML content using OkHttp
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
                )
                .build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val html = response.body?.string()

                // Parse the HTML with Jsoup
                val document: Document = Jsoup.parse(html)

                // Extract job postings (adjust the selectors as needed)
                val jobElements = document.select("div.base-card")
                for (jobElement in jobElements) {
                    val title = jobElement.select("h3.base-search-card__title").text()
                    val company = jobElement.select("h4.base-search-card__subtitle").text()
                    val location = jobElement.select("span.job-search-card__location").text()
                    val date = jobElement.select("time.job-search-card__listdate--new").attr("datetime")
                    val timePosted = jobElement.select("time.job-search-card__listdate--new").text()

                    val job = Job(title, company, location, date, timePosted)
                    jobList.add(job)
                }
                response.close()
                return jobList.toList()
            } else {
                Log.e("Error", "Failed to fetch jobs. Code: ${response.code}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("Error", "Error: ${e.message}")
            return emptyList()
        }
    }
}