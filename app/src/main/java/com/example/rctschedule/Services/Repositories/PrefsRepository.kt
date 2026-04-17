package com.example.rctschedule.Services.Repositories

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

inline fun <reified T> createJsonSerializer(defaultValue: T, json: Json = Json) = object : Serializer<T> {
    override val defaultValue: T = defaultValue

    override suspend fun readFrom(input: InputStream): T {
        try {
            return json.decodeFromString<T>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read", serialization)
        }
    }

    override suspend fun writeTo(
        t: T,
        output: OutputStream,
    ) {
        output.write(
            Json.encodeToString(t)
                .encodeToByteArray()
        )
    }
}

abstract class PrefsRepository<T: Any>(
    private val context: Context,
    private val contentPrefsName: String,
    private val serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null
){

    val Context.dataStore by dataStore(
        fileName = contentPrefsName,
        serializer = serializer,
        corruptionHandler = corruptionHandler)


    val valueFlow: Flow<T> = context.dataStore.data
        .distinctUntilChanged()


    suspend fun get() : T {
        return valueFlow.first()
    }

    suspend fun set(value: T) {
        context.dataStore.updateData { value }
    }
}