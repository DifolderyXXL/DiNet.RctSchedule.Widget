package com.example.rctschedule.Services.Parsing.lowlevel

import android.util.Log
import com.example.rctschedule.Services.Network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.inject.Inject

class StandardDownloader @Inject constructor(
    private val apiClient: ApiClient
) {
    suspend fun download(url: URL): ByteArray? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = apiClient.client.newCall(request).execute()

            if(!response.isSuccessful){
                return@withContext null
            }

            return@withContext response.body.bytes()
        }
    }
}