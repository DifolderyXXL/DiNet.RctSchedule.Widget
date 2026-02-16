package com.example.rctschedule.Model

import android.content.Context
import com.example.rctschedule.State
import com.example.rctschedule.TransformTable
import com.example.rctschedule.loadData
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetModelRepository  @Inject constructor(){
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetModelRepositoryEntrypoint {
        fun widgetModelRepository(): WidgetModelRepository
    }

    companion object {
        fun get(applicationContext: Context): WidgetModelRepository {
            var widgetModelRepositoryEntryoint: WidgetModelRepositoryEntrypoint = EntryPoints.get(
                applicationContext,
                WidgetModelRepositoryEntrypoint::class.java,
            )
            return widgetModelRepositoryEntryoint.widgetModelRepository()
        }
    }


    private val _destinations = HashMap<Int, MutableStateFlow<State>>()

    public fun loadOrCreate(id: Int) : StateFlow<State>
    {
        return loadOrCreateInternal(id)
    }

    private fun loadOrCreateInternal(id: Int) : MutableStateFlow<State>
    {
        if(_destinations.contains(id))
            return _destinations.get(id)!!

        val value = MutableStateFlow<State>(State.Loading)
        _destinations[id] = value

        return value
    }

    public fun updateModel(id: Int)
    {
        runBlocking {
            val data = loadData()

            val content = loadOrCreateInternal(id)

            content.value = State.Completed(TransformTable(data!!))
        }
    }
}