package com.example.rctschedule.Services.Network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(RetryInterceptor(5))
        .build()
}

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var retryCount = 0
        var exception: IOException? = null

        while (retryCount < maxRetries) {
            try {
                val response = chain.proceed(chain.request())

                if (response.isSuccessful) {
                    return response
                }

                response.close()
                retryCount++

                if (retryCount >= maxRetries) {
                    return response
                }

            } catch (e: IOException) {
                exception = e
                retryCount++

                if (retryCount >= maxRetries) {
                    throw IOException("Failed after $maxRetries retries", exception)
                }
            }

            Thread.sleep(1000L * retryCount)
        }

        throw exception ?: IOException("Unknown error")
    }
}