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
import com.google.firebase.auth.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    // Firebase Functions를 사용할 경우에는 다음 코드 주석 해제
    // private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
    // Firebase Functions Emulator를 사용할 경우에는 다음 코드 주석 해제
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

        /* Emulator에서 실행할 경우 아래 코드 주석 해제 */
        // functions.useEmulator("10.0.2.2", 5001)
    }

    /**
     * Google 계정 연동 로그인용 Launcher를 생성하는 함수
     *
     * @param context 해당 Launcher를 실행할 Activity의 Context
     * @param onSuccess 구글 계정 연동 성공 시 실행할 Callback 함수
     * @param onFailure 구글 계정 연동 실패 시 실행할 Callback 함수
     * @return Parameters를 이용하여 생성된 ActivityResultLauncher<Intent>
     */
    fun createGoogleLoginLauncher(
        context: Context,
        onSuccess: (responseCode: String) -> Unit,
        onFailure: (responseCode: String) -> Unit
    ): ActivityResultLauncher<Intent> {
        return (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val googleSignInTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val credential = GoogleAuthProvider.getCredential(
                    googleSignInTask.getResult(ApiException::class.java)!!.idToken!!,
                    null
                )
                CoroutineScope(Dispatchers.Main).launch {
                    val authResult: AuthResult = withContext(Dispatchers.IO) {
                        auth.signInWithCredential(credential).await()
                    }
                    val idTokenResult: GetTokenResult =
                        withContext(Dispatchers.IO) { authResult.user!!.getIdToken(true).await() }
                    val loginResult: Result<String> =
                        withContext(Dispatchers.IO) { login(idTokenResult.token!!) }

                    if (loginResult.getOrNull()!! == FirebaseConstants.ResponseCodes.UNKNOWN_ERROR)
                        onFailure(FirebaseConstants.ResponseCodes.UNKNOWN_ERROR)
                    else {
                        val userResult: Result<PaeParoUser> =
                            withContext(Dispatchers.IO) { getUser(authResult.user!!.uid) }

                        if (userResult.isFailure) {
                            onFailure(FirebaseConstants.ResponseCodes.USER_NOT_FOUND)
                        } else {
                            context.getPaeParo().userId = authResult.user!!.uid
                            context.getPaeParo().nickname = userResult.getOrNull()!!.nickname
                            onSuccess(loginResult.getOrNull()!!)
                        }
                    }
                }
            } catch (e: Exception) {
                onFailure(FirebaseConstants.ResponseCodes.UNKNOWN_ERROR)
            }
        }
    }

    /**
     * 로그인 요청을 보내는 함수
     *
     * @param idToken 발급받은 idToken
     * @return 로그인 결과(SUCCESS, NICKNAME_NOT_SET, DETAIL_INFO_NOT_SET, UNKNOWN_ERROR)
     */
    private suspend fun login(idToken: String): Result<String> {
        return try {
            val result = functions.getHttpsCallable("login")
                .call(
                    hashMapOf(
                        "id_token" to idToken
                    )
                ).await().data as Map<*, *>

            Result.success(result["result"] as String)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Google 계정을 이용하여 로그인하는 함수
     *
     * @param context 해당 로그인 함수를 실행할 Activity의 Context
     * @param googleSignInLauncher 이전에 생성한 ActivityResultLauncher<Intent> (FirebaseManager.createGoogleLoginLauncher()을 이용하여 생성한 객체)
     */
    fun launchGoogleLoginTask(
        context: Context,
        googleSignInLauncher: ActivityResultLauncher<Intent>
    ) {
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
     * 특정 사용자를 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return 성공 시 사용자 정보, 실패 시 Exception 반환
     */
    suspend fun getUser(userId: String): Result<PaeParoUser> {
        return try {
            val userRef = firestoreUsersRef.document(userId).get().await()

            val user = userRef.toObject(PaeParoUser::class.java)
            user!!.userId = userRef.id
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 사용자 닉네임을 설정하는 함수
     *
     * @param userId 사용자 ID
     * @param nickname 업데이트할 닉네임
     * @return 닉네임 설정 결과((SUCCESS, NICKNAME_ALREADY_IN_USE, UNKNOWN_ERROR)
     */
    suspend fun updateUserNickname(
        userId: String,
        nickname: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateUserNickname")
                    .call(
                        hashMapOf(
                            "user_id" to userId,
                            "nickname" to nickname
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 사용자 세부정보를 설정하는 함수
     *
     * @param userId 사용자 ID
     * @param updateFields 업데이트할 정보
     * @return 세부정보 설정 결과(SUCCESS, UNKNOWN_ERROR)
     */
    suspend fun updateUserDetailInfo(
        userId: String,
        updateFields: Map<String, Any?>
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateUserDetailInfo")
                    .call(
                        hashMapOf(
                            "user_id" to userId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 사용자가 포함된 여행 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return 성공 시 여행 목록 반환, 실패 시 Exception 반환
     */
    suspend fun getUserTrips(userId: String): Result<List<Trip>> {
        return try {
            val tripsRef =
                firestoreTripsRef.whereArrayContains("members", userId).get()
                    .await()

            val trips = mutableListOf<Trip>()
            for (tripRef in tripsRef) {
                val trip = tripRef.toObject(Trip::class.java)
                trip.tripId = tripRef.id
                trips.add(trip)
            }
            Result.success(trips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 사용자가 작성한 게시물 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return 성공 시 게시물 목록 반환, 실패 시 Exception 반환
     */
    suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val postsRef =
                firestorePostsRef.whereEqualTo("userId", userId).get()
                    .await()

            val posts = mutableListOf<Post>()
            for (postRef in postsRef) {
                val post = postRef.toObject(Post::class.java)
                post.postId = postRef.id
                posts.add(post)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 게시글에 특정 사용자가 좋아요를 추가하는 함수
     *
     * @param postId
     * @param userId
     * @return 좋아요 결과(SUCCESS, POST_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("likePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 게시글에 특정 사용자가 좋아요를 취소하는 함수
     *
     * @param postId
     * @param userId
     * @return 좋아요 취소 결과(SUCCESS, POST_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun cancelLikePost(postId: String, userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("cancelLikePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 사용자가 좋아요한 게시물 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return 성공 시 게시물 목록 반환, 실패 시 Exception 반환
     */
    suspend fun getUserLikedPosts(userId: String): Result<List<Post>> {
        return try {
            val userRef = firestoreUsersRef.document(userId).get().await()
            val user = userRef.toObject(PaeParoUser::class.java)!!

            val posts = mutableListOf<Post>()
            for (postId in user.likedPosts) {
                val postRef = firestorePostsRef.document(postId).get().await()
                val post = postRef.toObject(Post::class.java)!!
                post.postId = postRef.id
                posts.add(post)
            }

            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 사용자가 작성한 댓글 목록을 불러오는 함수
     *
     * @param userId 사용자 ID
     * @return 성공 시 댓글 목록 반환, 실패 시 Exception 반환
     */
    suspend fun getUserComments(userId: String): Result<List<Comment>> {
        return try {
            val commentsRef = firestoreCommentsRef.whereEqualTo("userId", userId).get().await()

            val comments = mutableListOf<Comment>()
            for (commentRef in commentsRef) {
                val comment = commentRef.toObject(Comment::class.java)
                comment.commentId = commentRef.id
                comments.add(comment)
            }

            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 닉네임으로 시작하는 사용자 5명을 불러오는 함수
     *
     * @param startWith 닉네임 시작 문자열
     * @return 성공 시 사용자 목록 반환, 실패 시 Exception 반환
     */
    suspend fun getUsersStartWith(startWith: String): Result<List<PaeParoUser>> {
        return try {
            val usersList = mutableListOf<PaeParoUser>()
            val usersListRef =
                firestoreUsersRef.orderBy("nickname").startAt(startWith).endAt(startWith + "\uf8ff")
                    .limit(5).get().await()

            usersListRef.documents.forEach {
                usersList.add(PaeParoUser(it.id, it.getString("nickname")!!))
            }

            Result.success(usersList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 새로운 여행을 생성하는 함수
     *
     * @param trip 생성할 여행 객체
     * @param userId 여행을 생성한 사용자 ID
     * @return 여행 생성 결과(TRIP_ID, UNKNOWN_ERROR)
     */
    suspend fun createTrip(trip: Trip, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("createTrip")
                    .call(
                        hashMapOf(
                            "trip" to trip.toMapWithoutTripId(),
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["trip_id"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 여행을 가져오는 함수
     *
     * @param tripId 가져올 여행 ID
     * @return 성공 시 여행 객체 반환, 실패 시 Exception 반환
     */
    suspend fun getTrip(tripId: String): Result<Trip> {
        return try {
            val tripRef = firestoreTripsRef.document(tripId).get().await()
            val trip = tripRef.toObject(Trip::class.java)!!
            trip.tripId = tripRef.id

            Result.success(trip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 여행을 수정하는 함수
     *
     * @param tripId 수정할 여행 ID
     * @param updateFields 수정할 요소의 이름과 값이 담긴 Map
     * @return 여행 수정 결과(SUCCESS, TRIP_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun updateTrip(tripId: String, updateFields: Map<String, Any?>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateTrip")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 여행을 삭제하는 함수
     *
     * @param tripId 삭제할 여행 ID
     * @return 여행 삭제 결과(SUCCESS, TRIP_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun deleteTrip(tripId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("deleteTrip")
                    .call(hashMapOf("trip_id" to tripId)).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 여행 초대를 수락하는 함수
     *
     * @param tripId 초대를 수락할 여행 ID
     * @param userId 수락하는 사용자 ID
     * @return 초대 수락 결과(SUCCESS, TRIP_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun acceptTripInvitation(tripId: String, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("acceptTripInvitation")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 여행 초대를 거절하는 함수
     *
     * @param tripId 초대를 거절할 여행 ID
     * @param userId 거절하는 사용자 ID
     * @return 초대 거절 결과(SUCCESS, TRIP_NOT_FOUND, UNKNOWN_ERROR)
     */
    suspend fun rejectTripInvitation(tripId: String, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("rejectTripInvitation")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 여행에 이벤트를 추가하는 함수
     *
     * @param userId 이벤트를 추가한 사용자 ID
     * @param tripId 이벤트를 추가할 여행 ID
     * @param event 추가할 이벤트 객체
     * @return 이벤트 추가 결과(EVENT_ID, UNKNOWN_ERROR)
     */
    suspend fun addEventToTrip(userId: String, tripId: String, event: Event): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("addEventToTrip")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "event" to event.toMapWithoutEventId(),
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["event_id"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 여행에서 이벤트를 제거하는 함수
     *
     * @param userId 이벤트를 제거한 사용자 ID
     * @param tripId 이벤트를 제거할 여행 ID
     * @param eventId 제거할 이벤트 ID
     * @return 이벤트 제거 결과(SUCCESS, UNKNOWN_ERROR)
     */
    suspend fun removeEventFromTrip(
        userId: String,
        tripId: String,
        eventId: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("removeEventFromTrip")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "event_id" to eventId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 특정 여행에 속한 특정 사용자의 위치를 업데이트하는 함수
     *
     * @param tripId 업데이트할 여행 ID
     * @param locationUpdateInfo 업데이트할 위치 정보
     * @return 위치 업데이트 결과(SUCCESS, UNKNOWN_ERROR)
     */
    suspend fun updateUserLocation(tripId: String, locationUpdateInfo: LocationUpdateInfo): Result<String>{
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateUserLocation")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "user_id" to locationUpdateInfo.userId,
                            "location_update_info" to locationUpdateInfo.toMapWithouUserId()
                        )
                    ).await().data as Map<*, *>

                Result.success(result["result"] as String)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
