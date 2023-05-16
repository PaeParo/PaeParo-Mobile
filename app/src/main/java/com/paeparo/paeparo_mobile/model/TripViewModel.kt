package com.paeparo.paeparo_mobile.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch

/**
 * TripViewModel class
 *
 * 불러온 여행 정보를 저장 및 관리하는 ViewModel 클래스
 */
class TripViewModel : ViewModel() {
    /**
     * Firebase 관련 처리 상태
     */
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    /**
     * 사용자가 멤버로 포함되어 있는 여행 목록
     */
    private val _tripList = MutableLiveData<List<Trip>>()
    val tripList: LiveData<List<Trip>> = _tripList

    /**
     * 사용자가 초대되어 있는 여행 목록
     */
    private val _invitationList = MutableLiveData<List<Trip>>()
    val invitationList: LiveData<List<Trip>> = _invitationList

    /**
     * FirebaseManager에서 발생한 에러
     */
    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error


    /**
     * Firebase에서 사용자 ID를 기반으로 가져온 여행 목록을 분리해서 저장하는 함수
     *
     * @param userId 사용자 ID
     */
    fun loadTrips(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val userTripsResult = FirebaseManager.getUserTrips(userId)

            if (userTripsResult.isSuccess) {
                val userTrips = userTripsResult.data
                _tripList.value = userTrips?.first ?: emptyList()
                _invitationList.value = userTrips?.second ?: emptyList()
            } else {
                _error.value = userTripsResult.error
            }
            _loading.value = false
        }
    }

    /**
     * 여행 초대 수락 후 멤버 여행 목록, 초대 목록을 업데이트 하는 함수
     *
     * @param trip 수락할 Trip 객체
     * @param userId 사용자 ID
     */
    fun acceptInvitation(trip: Trip, userId: String) {
        viewModelScope.launch {
            val acceptInvitationResult = FirebaseManager.acceptTripInvitation(trip.tripId, userId)
            if (acceptInvitationResult.isSuccess) {
                // Make a copy of the list before modifying it
                val newTripList = _tripList.value?.toMutableList()?.apply { add(trip) }
                val newInvitationList =
                    _invitationList.value?.toMutableList()?.apply { remove(trip) }

                _tripList.value = newTripList
                _invitationList.value = newInvitationList
            } else {
                _error.value = acceptInvitationResult.error
            }
        }
    }

    /**
     * 여행 초대 거절 후 초대 목록을 업데이트 하는 함수
     *
     * @param trip 거절할 Trip 객체
     * @param userId 사용자 ID
     */
    fun declineInvitation(trip: Trip, userId: String) {
        viewModelScope.launch {
            val declineInvitationResult = FirebaseManager.rejectTripInvitation(trip.tripId, userId)
            if (declineInvitationResult.isSuccess) {
                // Make a copy of the list before modifying it
                val newInvitationList =
                    _invitationList.value?.toMutableList()?.apply { remove(trip) }

                _invitationList.value = newInvitationList
            } else {
                _error.value = declineInvitationResult.error
            }
        }
    }

    /**
     * 여행 삭제 후 여행 목록을 업데이트 하는 함수
     *
     * @param trip 삭제할 Trip 객체
     */
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            val deleteTripResult = FirebaseManager.deleteTrip(trip.tripId)
            if (deleteTripResult.isSuccess) {
                _tripList.value = _tripList.value?.toMutableList()?.apply { remove(trip) }
            } else {
                _error.value = deleteTripResult.error
            }
        }
    }
}