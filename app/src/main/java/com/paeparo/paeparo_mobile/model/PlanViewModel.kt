package com.paeparo.paeparo_mobile.model

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paeparo.paeparo_mobile.application.PaeParo
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.util.DateUtil.calPeriod
import com.paeparo.paeparo_mobile.util.DateUtil.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate

enum class MODE {
    DISPLAY, EDIT,
}


class PlanViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * @param VIEW : 보기 모드
     * @param EDIT : 수정 모드
     * @param CLONE : 수정이 불가능하고 복사가 가능한 보기 모드
     * @param POST : 지난 일정이며, 포스트 작성이 가능한 모드
     *
     */
    enum class MODE {
        VIEW, EDIT, CLONE, POST

    }


    lateinit var trip: Trip
    private lateinit var context : Context
    private lateinit var paeparo : PaeParo
    private val _viewMode = MutableStateFlow<MODE>(MODE.VIEW)
    val viewMode: StateFlow<MODE>
        get() = _viewMode

    private val _eventMap =
        MutableStateFlow<MutableMap<LocalDate, MutableStateFlow<MutableList<Event>>>>(
            mutableMapOf()
        )
    val eventMap: StateFlow<Map<LocalDate, StateFlow<List<Event>>>>
        get() = _eventMap

    val localDateList : List<LocalDate>
        get() = eventMap.value.keys.toList()

    init{
        context = application.applicationContext
        paeparo = context.getPaeParo()
    }
    private fun initEventMap(trip: Trip): MutableMap<LocalDate, MutableStateFlow<MutableList<Event>>> {
        val map: MutableMap<LocalDate, MutableStateFlow<MutableList<Event>>> = mutableMapOf()

        var tripEvents: List<Event> = listOf()

        val startDate = trip.startDate.toLocalDate()
        val EndDate = trip.endDate.toLocalDate()

        // map 초기화 (날짜별 List생성)
        repeat(EndDate.calPeriod(startDate)) {
            map[startDate.plusDays(it.toLong())] = MutableStateFlow<MutableList<Event>>(mutableListOf())
        }

        //getting Events
        val job = CoroutineScope(Dispatchers.IO).async {
//            Timber.d("PlanViewModel -> initEventMap() -> CorutinScope Started")
            FirebaseManager.getTripEvents(trip.tripId).data
//            Timber.d("PlanViewModel -> initEventMap() -> CorutinScope end \n\t eventList: $result")tripEvents = result!!
        }

        runBlocking {
            tripEvents = job.await()!!
        }

        tripEvents.forEach { event ->
            Timber.d("PlanViewModel -> initEventMap() -> tripEvents.forEach called")
            val eventStartDate = event.startTime.toLocalDate()
            map.computeIfAbsent(eventStartDate) { MutableStateFlow<MutableList<Event>>(mutableListOf()) }.value.add(event)
        }
        Timber.d("PlanViewModel -> initEventMap() Ended")

        return map.also{
            Timber.d("PlanViewModel -> initEvent() -> map returned")
            for((key,values) in it){
                Timber.d("\t key : $key(size:${values.value.size})")
                    for(value in values.value){
                        Timber.d("\t\t value : $value")
                    }

            }
        }
    }

    /**
     * Init view model by trip
     * Trip을 통해 EventMap을 초기화
     * @param trip
     */
    fun initViewModelByTrip(trip: Trip) {
        this.trip = trip.also{
            Timber.d("initViewModeByTrip() -> trip id: ${trip.tripId}")
        }
        _eventMap.tryEmit(initEventMap(trip))
    }

    fun changeViewMode(mode: MODE) {
        _viewMode.tryEmit(mode)
    }

    fun updateEventListTo(eventList : List<Event>, eventId: String, localDate: LocalDate) {
        _eventMap.value[localDate]?.tryEmit(eventList.toMutableList())

        val job = CoroutineScope(Dispatchers.IO).async {
            FirebaseManager.removeEventFromTrip(userId=paeparo.userId, tripId=trip.tripId, eventId= eventId )

        }
        runBlocking {
            val t = job.await()

            Timber.d("PlanViewModel -> updateEventListTo() -> tripId : ${trip.tripId}, event_id : $eventId, Success : ${t.isSuccess}, FailureType : ${t.error}")
        }
    }
}
