package com.paeparo.paeparo_mobile.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.model.*
import kotlinx.coroutines.tasks.await
import com.paeparo.paeparo_mobile.model.User as PaeParoUser


/**
 * FirebaseManager class
 *
 * Firebase와 관련된 다양한 서비스 인스턴스를 포함하고 있으며 이를 이용하여 여러 기능들을 제공하는 클래스
 */
@SuppressLint("StaticFieldLeak")
object FirebaseManager {

    /**
     * Firebase Authentication Module
     */
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Firebase Realtime Database Module
     */
    private val realtimeDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    /**
     * Firebase Cloud Firestore Module
     */
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Firebase Storage Module
     */
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /**
     * Firebase Cloud Functions Module
     */
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    /**
     * Firebase Cloud Messaging Module
     */
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()

    /**
     * Application Context
     */
    private lateinit var applicationContext: Context

    /**
     * Google 로그인 시 사용할 GoogleSignInOptions
     */
    private lateinit var gso: GoogleSignInOptions

    /**
     * Cloud Firestore의 TripUpdates 컬렉션에 대한 참조
     */
    private val firestoreTripUpdateRef = firestore.collection("trip_update")

    /**
     * Cloud Firestore의 LocationUpdates 컬렉션에 대한 참조
     */
    private val firestoreLocationUpdateRef = firestore.collection("location_update")

    /**
     * Cloud Firestore의 Users 컬렉션에 대한 참조
     */
    private val firestoreUsersRef = firestore.collection("users")

    /**
     * Cloud Firestore의 Trips 컬렉션에 대한 참조
     */
    private val firestoreTripsRef = firestore.collection("trips")

    /**
     * Cloud Firestore의 Events 컬렉션에 대한 참조
     */
    private val firestoreEventsRef = firestore.collection("events")

    /**
     * Cloud Firestore의 Posts 컬렉션에 대한 참조
     */
    private val firestorePostsRef = firestore.collection("posts")

    /**
     * Cloud Firestore의 Comments 컬렉션에 대한 참조
     */
    private val firestoreCommentsRef = firestore.collection("comments")


    /**
     * TripUpdates에 대한 변경사항을 수신하는 Listener
     */
    private var tripUpdateListener: ListenerRegistration? = null

    /**
     * LocationUpdates에 대한 변경사항을 수신하는 Listener
     */
    private var locationUpdateListener: ListenerRegistration? = null

    /**
     * FirebaseManager 사용 전 객체 내 요소들을 초기화하는 함수
     *
     * @param context Application Context
     */
    fun initializeManager(context: Context) {
        applicationContext = context.applicationContext
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
            .requestEmail().build()
    }

    /**
     * Google 계정 연동 로그인용 Launcher를 생성하는 함수
     *
     * @param context 해당 Launcher를 실행할 Activity의 Context
     * @param onSuccess 로그인 성공 시 실행할 Callback 함수
     * @param onFailure 로그인 실패 시 실행할 Callback 함수
     * @return Parameters를 이용하여 생성된 ActivityResultLauncher<Intent>
     */
    fun createGoogleLoginLauncher(
        context: Context, onSuccess: () -> Unit, onFailure: () -> Unit
    ): ActivityResultLauncher<Intent> {
        return (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val googleSignInTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = googleSignInTask.getResult(ApiException::class.java)!!
                // Google 로그인에서 얻은 ID 토큰으로 Firebase 인증 처리
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                auth.signInWithCredential(credential).addOnCompleteListener { firebaseAuthTask ->
                    if (firebaseAuthTask.isSuccessful) {
                        context.getPaeParo().userId = auth.currentUser!!.uid
                        onSuccess()
                    } else {
                        onFailure()
                    }
                }
            } catch (e: ApiException) {
                onFailure()
            }
        }
    }

    /**
     * 사용자 등록 여부를 확인하여 Firestore에 사용자를 추가하는 함수
     *
     * @return 등록 및 세부정보 입력 상태에 대한 확인 결과값
     */
    suspend fun checkCurrentUserRegistration(context: Context): FirebaseConstants.CheckRegistrationResult {
        return try {
            val userRef =
                firestoreUsersRef.document(context.getPaeParo().userId).get().await()

            if (!userRef.exists()) { // 사용자가 등록되어 있지 않을 경우, 사용자 등록 및 NICKNAME_NOT_REGISTERED 반환
                val newUser = PaeParoUser()
                firestoreUsersRef.document(context.getPaeParo().userId)
                    .set(newUser.toMapWithoutUserId()).await()
                return FirebaseConstants.CheckRegistrationResult.NicknameNotSet
            }

            val user = userRef.toObject(PaeParoUser::class.java)

            when {
                user!!.nickname.isEmpty() -> FirebaseConstants.CheckRegistrationResult.NicknameNotSet
                user.age == 0 -> FirebaseConstants.CheckRegistrationResult.DetailInfoNotSet
                else -> {
                    context.getPaeParo().nickname = user.nickname
                    FirebaseConstants.CheckRegistrationResult.Registered
                }
            }
        } catch (e: Exception) {
            FirebaseConstants.CheckRegistrationResult.OtherError(e)
        }
    }

    /**
     * Google 계정을 이용하여 로그인하는 함수
     *
     * @param context 해당 로그인 함수를 실행할 Activity의 Context
     * @param googleSignInLauncher 이전에 생성한 ActivityResultLauncher<Intent> (FirebaseManager.createGoogleLoginLauncher()을 이용하여 생성한 객체)
     */
    fun loginWithGoogle(context: Context, googleSignInLauncher: ActivityResultLauncher<Intent>) {
        // Google 로그인 창 표시
        googleSignInLauncher.launch(GoogleSignIn.getClient(context, gso).signInIntent)
    }

    /**
     * 현재 로그인된 Google 계정을 PaeParo에서 로그아웃하는 함수
     *
     * @param context 해당 로그아웃 함수를 실행할 Activity의 Context
     */
    fun logoutWithGoogle(context: Context) {
        context.getPaeParo().clearUserInfo()
        auth.signOut() // Firebase 인증 서비스에서 현재 로그인된 사용자를 로그아웃
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
            .revokeAccess() // Google 계정에서 연결을 해제하여 로그아웃
    }

    /**
     * 현재 로그인된 사용자를 가져오는 함수
     *
     * @return 로그인된 사용자가 존재할 경우 FirebaseUser 객체 반환, 아닐 경우 null 반환
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * TripUpdate에 대한 업데이트 리스너를 시작하는 함수
     *
     * @param tripId 업데이트를 감지할 여행 ID
     * @param onUpdate 업데이트 시 호출할 함수
     */
    fun startTripUpdateListener(tripId: String, onUpdate: (TripUpdate) -> Unit) {
        tripUpdateListener =
            firestoreTripUpdateRef.document(tripId).addSnapshotListener { value, error ->
                if (error != null) { // 에러가 발생할 경우
                    Log.e("TripUpdateListener", "Listener 등록 실패", error)
                    return@addSnapshotListener
                }

                if (value != null && value.exists()) { // 데이터가 있을 경우
                    val tripUpdateInfo = value.toObject(TripUpdateInfo::class.java)
                    if (tripUpdateInfo != null) {
                        onUpdate(TripUpdate(tripId, tripUpdateInfo))
                    }
                } else { // 데이터가 없을 경우
                    Log.d("TripUpdateListener", "데이터가 없습니다")
                }
            }
    }

    /**
     * TripUpdates에 대한 업데이트 리스너를 중지하는 함수
     */
    fun stopTripUpdateListener() {
        tripUpdateListener?.remove()
        tripUpdateListener = null
    }

    /**
     * LocationUpdate에 대한 위치정보 업데이트 리스너를 시작하는 함수
     *
     * @param tripId 업데이트를 감지할 여행 ID
     * @param onUpdate 업데이트 시 호출할 함수
     */
    fun startLocationUpdateListener(tripId: String, onUpdate: (List<LocationUpdateInfo>) -> Unit) {
        val memberLocationsRef =
            firestoreLocationUpdateRef.document(tripId).collection("member_locations")

        locationUpdateListener = memberLocationsRef.addSnapshotListener { memberLocations, error ->
            if (error != null) {
                Log.w("LocationUpdateListener", "Listener 등록 실패", error)
                return@addSnapshotListener
            }

            if (memberLocations != null && !memberLocations.isEmpty) {
                val locationUpdateInfos = mutableListOf<LocationUpdateInfo>()

                for (memberLocation in memberLocations.documents) {
                    val locationUpdateInfo = memberLocation.toObject(LocationUpdateInfo::class.java)
                    if (locationUpdateInfo != null) {
                        locationUpdateInfos.add(locationUpdateInfo)
                    }
                }

                onUpdate(locationUpdateInfos)
            } else {
                Log.d("LocationUpdateListener", "데이터가 없습니다")
            }
        }
    }

    /**
     * 여행 동행자들의 위치정보 업데이트 리스너를 중지하는 함수
     */
    fun stopLocationUpdateListener() {
        locationUpdateListener?.remove()
        locationUpdateListener = null
    }


    /**
     * 현재 사용자 정보를 가져오는 함수
     *
     * @param context 함수를 실행할 Activity의 Context
     * @return 사용자 정보를 가져오는데 성공할 경우 User 객체 반환, 실패할 경우 Exception 반환
     */
    suspend fun getCurrentUserData(context: Context): Result<PaeParoUser> {
        return try {
            val userData =
                firestoreUsersRef.document(context.getPaeParo().userId).get().await()

            val user = userData.toObject(PaeParoUser::class.java)
            user!!.userId = userData.id
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 현재 사용자의 닉네임을 업데이트하는 함수
     *
     * @param context 함수를 실행할 Activity의 Context
     * @param nickname 업데이트할 닉네임
     * @return 닉네임 업데이트 처리결과
     */
    suspend fun updateCurrentUserNickname(
        context: Context,
        nickname: String
    ): FirebaseConstants.UpdateNicknameResult {
        return try {
            val existingUserWithNickname =
                firestoreUsersRef.whereEqualTo("nickname", nickname).get().await()

            if (!existingUserWithNickname.isEmpty) return FirebaseConstants.UpdateNicknameResult.DuplicateError

            firestoreUsersRef.document(context.getPaeParo().userId)
                .update("nickname", nickname).await()

            context.getPaeParo().nickname = nickname

            FirebaseConstants.UpdateNicknameResult.UpdateSuccess
        } catch (e: Exception) {
            FirebaseConstants.UpdateNicknameResult.OtherError(e)
        }
    }

    /**
     * 현재 사용자 세부정보를 업데이트하는 함수
     *
     * @param context 함수를 실행할 Activity의 Context
     * @param age 사용자 나이
     * @param gender 사용자 성별
     * @param travelStyle 사용자 여행 취향
     * @return 성공 여부
     */
    suspend fun updateCurrentUserDetailInfo(
        context: Context, age: Int, gender: String, travelStyle: List<String>
    ): Result<Unit> {
        return try {
            val currentUserRef = FirebaseFirestore.getInstance().collection("users")
                .document(context.getPaeParo().userId)

            currentUserRef.update(
                mapOf(
                    "age" to age, "gender" to gender, "travel_style" to travelStyle
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 닉네임이 특정 문자열로 시작하는 사용자들을 가져오는 함수
     *
     * @param startWith 닉네임 시작 문자열
     * @return 사용자 정보를 가져오는데 성공할 경우 User 객체 리스트 반환, 실패할 경우 Exception 반환
     */
    suspend fun getUsersStartWith(startWith: String): Result<List<PaeParoUser>> {
        return try {
            val resultList = mutableListOf<PaeParoUser>()
            val filteredUserList =
                firestoreUsersRef.orderBy("nickname").startAt(startWith).endAt(startWith + "\uf8ff")
                    .limit(5).get().await()

            for (user in filteredUserList.documents) {
                resultList.add(PaeParoUser(user.id, user.getString("nickname")!!))
            }

            Result.success(resultList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 여행을 생성하는 함수
     *
     * @param context 함수를 실행할 Activity의 Context
     * @param trip 생성할 여행
     * @return 성공 여부
     */
    suspend fun createNewTrip(
        context: Context,
        trip: Trip
    ): Result<String> {
        return try {
            val batch = FirebaseFirestore.getInstance().batch()

            val newTripRef = firestoreTripsRef.document()
            batch.set(
                newTripRef,
                trip.toMapWithoutTripId()
            )

            val tripUpdatesRef = firestoreTripUpdateRef.document(newTripRef.id)
            batch.set(
                tripUpdatesRef,
                TripUpdateInfo(
                    context.getPaeParo().userId,
                    "",
                    TripUpdate.UpdateType.CREATE,
                    Timestamp.now().seconds
                )
            )

            batch.commit().await()

            Result.success(newTripRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * 현재 사용자가 속해있는 모든 여행 목록을 가져오는 함수
     *
     * @param context 함수를 실행할 Activity의 Context
     * @return 여행 목록을 가져오는데 성공할 경우 여행 목록 반환, 실패할 경우 Exception 반환
     */
    suspend fun getCurrentUserTrips(context: Context): Result<List<Trip>> {
        return try {
            val tripsReference =
                firestoreTripsRef.whereArrayContains("members", context.getPaeParo().userId).get()
                    .await()

            val trips = mutableListOf<Trip>()
            for (trip in tripsReference) {
                val tripData = trip.toObject(Trip::class.java)
                tripData.tripId = trip.id
                trips.add(tripData)
            }
            Result.success(trips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 이벤트를 여행에 추가하는 함수
     *
     * @param tripId 이벤트를 추가할 여행 ID
     * @param event 추가할 이벤트 객체
     * @return 성공 여부
     */

    suspend fun addEventToTrip(
        context: Context, tripId: String, event: Event
    ): Result<String> {
        return try {
            val newEventRef =
                firestoreEventsRef.document(tripId).collection("trip_events").document()
            val tripUpdateRef = firestoreTripUpdateRef.document(tripId)

            val batch = FirebaseFirestore.getInstance().batch()
            batch.set(newEventRef, event.toMapWithoutEventId())
            batch.set(
                tripUpdateRef,
                TripUpdateInfo(
                    context.getPaeParo().userId,
                    "events/${tripId}/trip_events/${newEventRef.id}",
                    TripUpdate.UpdateType.ADD,
                    Timestamp.now().seconds
                )
            )

            batch.commit().await()
            Result.success(newEventRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 이벤트를 여행에서 제거하는 함수
     *
     * @param tripId 제거할 이벤트가 들어있는 여행 ID
     * @param eventId 제거할 이벤트 ID
     * @return 성공 여부
     */
    suspend fun removeEventFromTrip(
        context: Context, tripId: String, eventId: String
    ): Result<String> {
        return try {
            val eventRef =
                firestoreEventsRef.document(tripId).collection("trip_events").document(eventId)
            val tripUpdateRef = firestoreTripUpdateRef.document(tripId)

            val batch = FirebaseFirestore.getInstance().batch()
            batch.delete(eventRef)
            batch.set(
                tripUpdateRef,
                TripUpdateInfo(
                    context.getPaeParo().userId,
                    "events/${tripId}/trip_events/${eventId}",
                    TripUpdate.UpdateType.REMOVE,
                    Timestamp.now().seconds
                )
            )

            batch.commit().await()
            Result.success(eventId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Evnet 참조를 이용하여 해당 Event를 가져오는 함수
     *
     * @param eventReference Event 참조
     * @return Event를 가져오는데 성공할 경우 Event 객체 반환, 실패할 경우 Exception 반환
     */
    suspend fun getEventByReference(eventReference: String): Result<Event> {
        return try {
            val eventRef = FirebaseFirestore.getInstance()
                .document(eventReference)
                .get()
                .await()

            if (eventRef.exists()) { // 이벤트가 존재할 경우
                val event: Event? = when (eventRef["type"] as? Event.EventType) {
                    Event.EventType.PLACE -> eventRef.toObject(PlaceEvent::class.java)
                    Event.EventType.MOVE -> eventRef.toObject(MoveEvent::class.java)
                    Event.EventType.MEAL -> eventRef.toObject(PlaceEvent::class.java)
                    else -> eventRef.toObject(Event::class.java)
                }

                if (event != null) { // 이벤트 변환이 성공할 경우
                    Result.success(event)
                } else { // 이벤트 변환이 실패할 경우
                    Result.failure(Exception("이벤트 변환 중 오류가 발생했습니다"))
                }
            } else {
                Result.failure(Exception("해당 이벤트를 찾을 수 없습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
