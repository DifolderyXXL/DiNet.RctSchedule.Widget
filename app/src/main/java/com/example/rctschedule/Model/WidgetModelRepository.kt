package com.example.rctschedule.Model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.TransformExcelTable
import com.example.rctschedule.TransformExcelWeek
import com.example.rctschedule.TransformTable
import com.example.rctschedule.TransformWeek
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

sealed interface DataState {

    object Null: DataState
    object Loading : DataState
    object Error : DataState
    data class Completed(val table: TransformExcelWeek, val updateTime: Date) : DataState
}

sealed interface FetchState
{
    object Null : FetchState
    object Fetching : FetchState
    object Error : FetchState
    object Completed: FetchState
}

class WidgetViewModel(
    private val scheduleRepository: ScheduleDataRepository)
    : ViewModel()
{
    private val _fetchingState = MutableStateFlow<FetchState>(FetchState.Null)
    private val _state = MutableStateFlow<DataState>(DataState.Null);
    val state : StateFlow<DataState> = _state
    val fetchState : StateFlow<FetchState> = _fetchingState

    init {
        viewModelScope.launch {
            scheduleRepository.scheduleState
                .collect { state ->
                    when (state)
                    {
                        is ScheduleCacheData.Ok->{
                            _state.value = DataState.Completed(
                                TransformWeek(state.table, 0),
                                Date(state.lastUpdateTime))
                        }
                        else -> {
                            _state.value = DataState.Null
                        }
                    }
                }
        }
    }



    suspend fun requestUpdateInternal(force: Boolean = false)
    {
        _fetchingState.value = FetchState.Fetching

        val result = scheduleRepository.requestUpdate(force)

        if(result.isSuccess)
            _fetchingState.value = FetchState.Completed
        else
            _fetchingState.value = FetchState.Error
    }

    fun forceUpdateCommand()
    {
        viewModelScope.launch {
            requestUpdateInternal(true)
        }
    }
}

@Singleton
class WidgetModelRepository  @Inject constructor(val repository: ScheduleDataRepository){
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

    public fun loadOrCreate() : WidgetViewModel
    {
        return WidgetViewModel(repository)
    }

}