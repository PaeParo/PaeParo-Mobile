package com.paeparo.paeparo_mobile.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
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
import java.io.File
import java.util.UUID
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
     * Storage의 images에 대한 참조
     */
    private val storageImageRef = storage.reference.child("images")

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
        onSuccess: (result: FirebaseResult<String>) -> Unit,
        onFailure: (result: FirebaseResult<String>) -> Unit
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
                    val loginResult: FirebaseResult<String> = login(idTokenResult.token!!)

                    if (loginResult.isSuccess) {
                        context.getPaeParo().userId = authResult.user!!.uid
                        if (loginResult.type != FirebaseConstants.ResponseCodes.NICKNAME_NOT_SET) {
                            context.getPaeParo().nickname = loginResult.data!!
                        }
                        onSuccess(loginResult)
                    } else {
                        onFailure(loginResult)
                    }
                }
            } catch (e: Exception) {
                onFailure(FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e))
            }
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
     * 현재 로그인된 사용자의 프로필 URL을 가져오는 함수
     *
     * @return 로그인된 사용자가 존재할 경우 프로필 URL 반환, 아닐 경우 null 반환
     */
    fun getCurrentUserProfileUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
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
     * 1-01. 로그인 요청을 보내는 함수
     *
     * @param idToken 발급받은 idToken
     * @return Success Type: NICKNAME_NOT_SET, DETAIL_INFO_NOT_SET, ALL_DATA_SET / Failure Type: SERVER_ERROR. CLIENT_ERROR & Error Object
     */
    private suspend fun login(idToken: String): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("login")
                    .call(
                        hashMapOf(
                            "id_token" to idToken
                        )
                    ).await().data as Map<*, *>

                if (result["result"] == FirebaseConstants.ResponseCodes.SUCCESS
                    && (result["type"] == FirebaseConstants.ResponseCodes.ALL_DATA_SET || result["type"] == FirebaseConstants.ResponseCodes.DETAIL_INFO_NOT_SET)
                ) {
                    FirebaseResult.success(result["type"] as String, result["data"] as String)
                } else {
                    FirebaseResult.make(result)
                }
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-02. 특정 사용자를 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: User Object / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUser(userId: String): FirebaseResult<PaeParoUser> {
        return withContext(Dispatchers.IO) {
            try {
                val userRef = firestoreUsersRef.document(userId).get().await()

                val user = userRef.toObject(PaeParoUser::class.java)
                user!!.userId = userRef.id
                FirebaseResult.success(data = user)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-03. 특정 사용자 닉네임을 설정하는 함수
     *
     * @param userId 사용자 ID
     * @param nickname 업데이트할 닉네임
     * @return Success / Failure Type: NICKNAME_ALREADY_IN_USE, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updateUserNickname(
        userId: String,
        nickname: String
    ): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateUserNickname")
                    .call(
                        hashMapOf(
                            "user_id" to userId,
                            "nickname" to nickname
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-04. 특정 사용자 세부정보를 설정하는 함수
     *
     * @param userId 사용자 ID
     * @param updateFields 업데이트할 정보
     * @return Success / Failure Type: SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updateUserDetailInfo(
        userId: String,
        updateFields: Map<String, Any?>
    ): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateUserDetailInfo")
                    .call(
                        hashMapOf(
                            "user_id" to userId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-05. 특정 사용자가 포함된 여행 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: Trip Object List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUserTrips(userId: String): FirebaseResult<Pair<List<Trip>, List<Trip>>> {
        return withContext(Dispatchers.IO) {
            try {
                val tripsMemberRef =
                    firestoreTripsRef.whereArrayContains("members", userId).get()
                        .await()

                val tripsInvitationRef =
                    firestoreTripsRef.whereArrayContains("invitations", userId).get().await()

                val tripsInMember = mutableListOf<Trip>()
                for (tripRef in tripsMemberRef) {
                    val trip = tripRef.toObject(Trip::class.java)
                    trip.tripId = tripRef.id
                    tripsInMember.add(trip)
                }

                val tripsInInvitation = mutableListOf<Trip>()
                for (tripRef in tripsInvitationRef) {
                    val trip = tripRef.toObject(Trip::class.java)
                    trip.tripId = tripRef.id
                    tripsInInvitation.add(trip)
                }

                FirebaseResult.success(data = Pair(tripsInMember, tripsInInvitation))
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-06. 특정 사용자가 작성한 게시물 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: Post Object List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUserPosts(userId: String): FirebaseResult<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val postsRef =
                    firestorePostsRef.whereEqualTo("userId", userId).get()
                        .await()

                val posts = mutableListOf<Post>()
                for (postRef in postsRef) {
                    val post = postRef.toObject(Post::class.java)
                    post.postId = postRef.id
                    posts.add(post)
                }
                FirebaseResult.success(data = posts)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-07. 특정 게시글에 특정 사용자가 좋아요를 추가하는 함수
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return Success / Failure Type: POST_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun likePost(postId: String, userId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("likePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-08. 특정 게시글에 특정 사용자가 좋아요를 취소하는 함수
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return Success / Failure Type: POST_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun cancelLikePost(postId: String, userId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("cancelLikePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-09. 특정 사용자가 좋아요한 게시물 목록을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: Post Object List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUserLikedPosts(userId: String): FirebaseResult<MutableList<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val userRef = firestoreUsersRef.document(userId).get().await()
                val user = userRef.toObject(PaeParoUser::class.java)!!

                val posts = mutableListOf<Post>()
                for (postId in user.likedPosts) {
                    val postRef = firestorePostsRef.document(postId).get().await()
                    val post = postRef.toObject(Post::class.java)!!
                    post.postId = postRef.id
                    posts.add(post)
                }

                FirebaseResult.success(data = posts)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-10. 특정 사용자 취향에 맞는 게시글을 가져오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: Post Object / Failure Type: SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun getPostByUserPreference(userId: String): FirebaseResult<Post> {
        return withContext(Dispatchers.IO) {
            try {
                FirebaseResult.success()
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-11. 특정 사용자가 작성한 댓글 목록을 불러오는 함수
     *
     * @param userId 사용자 ID
     * @return Success Data: Comment Object List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUserComments(userId: String): FirebaseResult<MutableList<Comment>> {
        return withContext(Dispatchers.IO) {
            try {
                val commentsRef = firestoreCommentsRef.whereEqualTo("user_id", userId).get().await()

                val comments = mutableListOf<Comment>()
                for (commentRef in commentsRef) {
                    val comment = commentRef.toObject(Comment::class.java)
                    comment.commentId = commentRef.id
                    comments.add(comment)
                }

                FirebaseResult.success(data = comments)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 1-12. 특정 닉네임으로 시작하는 사용자 5명을 불러오는 함수
     *
     * @param startWith 닉네임 시작 문자열
     * @return Success Data: User Object List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getUsersStartWith(startWith: String): FirebaseResult<List<PaeParoUser>> {
        return withContext(Dispatchers.IO) {
            try {
                val userList = mutableListOf<PaeParoUser>()
                val userListRef =
                    firestoreUsersRef.orderBy("nickname").startAt(startWith)
                        .endAt(startWith + "\uf8ff")
                        .limit(5).get().await()

                userListRef.documents.forEach {
                    userList.add(
                        PaeParoUser(
                            it.id,
                            it.getString("nickname")!!,
                            it.getString("thumbnail")!!
                        )
                    )
                }

                FirebaseResult.success(data = userList)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-01. 새로운 여행을 생성하는 함수
     *
     * @param trip 생성할 여행 객체
     * @param userId 여행을 생성한 사용자 ID
     * @return Success Data: Trip ID / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun createTrip(trip: Trip, userId: String): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val tripRef = firestoreTripsRef.document()

                firestore.runBatch { batch ->
                    batch.set(tripRef, trip)
                    batch.set(
                        firestoreTripUpdateRef.document(tripRef.id),
                        TripUpdateInfo(userId, "", TripUpdate.UpdateType.CREATE, Timestamp.now())
                    )
                }.await()

                FirebaseResult.success(data = tripRef.id)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-02. 특정 여행을 가져오는 함수
     *
     * @param tripId 가져올 여행 ID
     * @return Success Data: Trip Object / Failure Type: TRIP_NOT_FOUND, CLIENT_ERROR & Error Object
     */
    suspend fun getTrip(tripId: String): FirebaseResult<Trip> {
        return withContext(Dispatchers.IO) {
            try {
                val tripRef = firestoreTripsRef.document(tripId).get().await()
                val trip = tripRef.toObject(Trip::class.java)!!
                trip.tripId = tripRef.id

                FirebaseResult.success(data = trip)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-03. 특정 여행을 수정하는 함수
     *
     * @param tripId 수정할 여행 ID
     * @param updateFields 수정할 요소의 이름과 값이 담긴 Map
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updateTrip(
        tripId: String,
        updateFields: Map<String, Any?>
    ): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateTrip")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-04. 특정 여행을 삭제하는 함수
     *
     * @param tripId 삭제할 여행 ID
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun deleteTrip(tripId: String): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("deleteTrip")
                    .call(hashMapOf("trip_id" to tripId)).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-05. 여행 초대를 수락하는 함수
     *
     * @param tripId 초대를 수락할 여행 ID
     * @param userId 수락하는 사용자 ID
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun acceptTripInvitation(tripId: String, userId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("acceptTripInvitation")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-06. 여행 초대를 거절하는 함수
     *
     * @param tripId 초대를 거절할 여행 ID
     * @param userId 거절하는 사용자 ID
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun rejectTripInvitation(tripId: String, userId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("rejectTripInvitation")
                    .call(
                        hashMapOf(
                            "trip_id" to tripId,
                            "user_id" to userId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-07. 특정 여행에 이벤트를 추가하는 함수
     *
     * @param userId 이벤트를 추가한 사용자 ID
     * @param tripId 이벤트를 추가할 여행 ID
     * @param event 추가할 이벤트 객체
     * @return Success Data: Event ID / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun createEvent(
        userId: String,
        tripId: String,
        event: Event
    ): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val eventRef =
                    firestoreEventsRef.document(tripId).collection("trip_events").document()

                firestore.runBatch { batch ->
                    batch.set(eventRef, event)
                    batch.set(
                        firestoreTripUpdateRef.document(tripId),
                        TripUpdateInfo(
                            userId,
                            eventRef.path,
                            TripUpdate.UpdateType.ADD,
                            Timestamp.now()
                        )
                    )
                }

                FirebaseResult.success(data = eventRef.id)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-08. 특정 여행에서 이벤트를 제거하는 함수
     *
     * @param userId 이벤트를 제거한 사용자 ID
     * @param tripId 이벤트를 제거할 여행 ID
     * @param eventId 제거할 이벤트 ID
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun removeEventFromTrip(
        userId: String,
        tripId: String,
        eventId: String
    ): FirebaseResult<Unit> {
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

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-09. 특정 여행에 속한 모든 이벤트를 불러오는 함수
     *
     * @param tripId 이벤트를 불러올 여행 ID
     * @return Success Data: Event List / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun getTripEvents(tripId: String): FirebaseResult<List<Event>> {
        return withContext(Dispatchers.IO) {
            try {
                val eventList = mutableListOf<Event>()
                val eventListRef = firestoreEventsRef.whereEqualTo("trip_id", tripId).get().await()

                eventListRef.documents.forEach {
                    val event = it.toObject(Event::class.java)!!
                    event.eventId = it.id
                    eventList.add(event)
                }

                FirebaseResult.success(data = eventList)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 2-10. 특정 여행에 속한 특정 사용자의 위치를 업데이트하는 함수
     *
     * @param tripId 업데이트할 여행 ID
     * @param locationUpdateInfo 업데이트할 위치 정보
     * @return Success / Failure Type: TRIP_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updateUserLocation(
        tripId: String,
        locationUpdateInfo: LocationUpdateInfo
    ): FirebaseResult<Unit> {
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

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 3-01. 특정 이벤트 세부정보를 불러오는 함수
     *
     * @param eventId 이벤트 ID
     * @return Success Data: Event Object / Failure Type: SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun getEvent(eventId: String): FirebaseResult<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val eventRef = firestoreEventsRef.document(eventId).get().await()
                val event = eventRef.toObject(Event::class.java)!!
                event.eventId = eventRef.id

                FirebaseResult.success(data = event)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물을 추가하는 함수
     *
     * @param post 추가할 게시물
     * @param localImagePaths 추가할 이미지 파일들의 로컬 경로 목록
     * @return Success Data: Post ID / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun createPost(post: Post, localImagePaths: List<String>?): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val postRef = firestorePostsRef.document()

                if (localImagePaths != null) {
                    for (path in localImagePaths) {
                        val imageRef =
                            storageImageRef.child(
                                "${post.userId}/${post.postId}/${
                                    UUID.randomUUID()
                                }"
                            )
                        imageRef.putFile(Uri.fromFile(File(path))).await()
                        post.images.add(imageRef.downloadUrl.await().toString())
                    }
                }

                postRef.set(post).await()

                FirebaseResult.success(data = postRef.id)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 게시글 목록을 불러오는 함수
     *
     * @param startPostSnapshot 시작 게시물 Snapshot (null일 경우 가장 최신 게시물이 시작)
     * @param limit 불러올 게시물 수
     * @return Success Data: Post List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getPostsInRange(
        startPostSnapshot: DocumentSnapshot?,
        limit: Long
    ): FirebaseResult<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val postList = mutableListOf<Post>()

                val postListRef = if (startPostSnapshot == null) {
                    firestorePostsRef.orderBy("created_at", Query.Direction.DESCENDING)
                        .limit(limit).get().await()
                } else {
                    firestorePostsRef.orderBy("created_at", Query.Direction.DESCENDING)
                        .startAfter(startPostSnapshot).limit(limit).get().await()
                }

                postListRef.documents.forEach {
                    val post = it.toObject(Post::class.java)!!
                    post.postId = it.id
                    postList.add(post)
                }

                FirebaseResult.success(
                    data = mapOf(
                        "posts" to postList,
                        "last_post_snapshot" to postListRef.last()
                    )
                )
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 좋아요가 가장 많은 게시물 5개를 불러옴
     *
     * @return Success Data: Post List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getTop5LikedPosts(): FirebaseResult<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val postList = mutableListOf<Post>()
                val postListRef =
                    firestorePostsRef.orderBy("like_count", Query.Direction.DESCENDING).limit(5)
                        .get().await()

                postListRef.documents.forEach {
                    val post = it.toObject(Post::class.java)!!
                    post.postId = it.id
                    postList.add(post)
                }

                FirebaseResult.success(data = postList)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물을 수정하는 함수
     *
     * @param postId 수정할 게시물 ID
     * @param updateFields 수정할 필드
     * @return Success / Failure Type: POST_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updatePost(postId: String, updateFields: Map<String, Any?>): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updatePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물을 삭제하는 함수
     *
     * @param postId 삭제할 게시물 ID
     * @return Success / Failure Type: POST_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun deletePost(postId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("deletePost")
                    .call(
                        hashMapOf(
                            "post_id" to postId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물을 불러오는 함수
     *
     * @param postId 불러올 게시물 ID
     * @return Success Data: Post / Failure Type: POST_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun getPost(postId: String): FirebaseResult<Post> {
        return withContext(Dispatchers.IO) {
            try {
                val postRef = firestorePostsRef.document(postId).get().await()
                val post = postRef.toObject(Post::class.java)!!
                post.postId = postRef.id

                FirebaseResult.success(data = post)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물의 댓글 목록을 불러오는 함수
     *
     * @param postId 댓글을 불러올 게시물 ID
     * @param startCommentId 시작 댓글 ID (null일 경우 가장 최신 댓글이 시작)
     * @param limit 불러올 댓글 수
     * @return Success Data: Comment List / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun getPostcommentsInRange(
        postId: String,
        startCommentId: String?,
        limit: Int
    ): FirebaseResult<List<Comment>> {
        return withContext(Dispatchers.IO) {
            try {
                val commentList = mutableListOf<Comment>()

                val commentListRef = if (startCommentId == null) {
                    firestoreCommentsRef.whereEqualTo("post_id", postId)
                        .orderBy("created_at", Query.Direction.DESCENDING).limit(limit.toLong())
                        .get().await()
                } else {
                    firestoreCommentsRef.whereEqualTo("post_id", postId)
                        .orderBy("created_at", Query.Direction.DESCENDING)
                        .startAfter(startCommentId)
                        .limit(limit.toLong()).get().await()
                }

                commentListRef.documents.forEach {
                    val comment = it.toObject(Comment::class.java)!!
                    comment.commentId = it.id
                    commentList.add(comment)
                }

                FirebaseResult.success(data = commentList)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 게시물에 댓글을 추가하는 함수
     *
     * @param comment 추가할 댓글 객체
     * @return Success Data: Comment ID / Failure Type: CLIENT_ERROR & Error Object
     */
    suspend fun createComment(comment: Comment): FirebaseResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val commentRef = firestoreCommentsRef.document()

                commentRef.set(comment.toMapWithoutCommentId()).await()

                FirebaseResult.success(data = commentRef.id)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 댓글을 수정하는 함수
     *
     * @param commentId 수정할 댓글 ID
     * @param updateFields 수정할 필드
     * @return Success / Failure Type: COMMENT_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun updateComment(
        commentId: String,
        updateFields: Map<String, Any?>
    ): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("updateComment")
                    .call(
                        hashMapOf(
                            "comment_id" to commentId,
                            "update_fields" to updateFields
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }

    /**
     * 특정 댓글을 삭제하는 함수
     *
     * @param commentId 삭제할 댓글 ID
     * @return Success / Failure Type: COMMENT_NOT_FOUND, SERVER_ERROR, CLIENT_ERROR & Error Object
     */
    suspend fun deleteComment(commentId: String): FirebaseResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = functions.getHttpsCallable("deleteComment")
                    .call(
                        hashMapOf(
                            "comment_id" to commentId
                        )
                    ).await().data as Map<*, *>

                FirebaseResult.make(result)
            } catch (e: Exception) {
                FirebaseResult.failure(FirebaseConstants.ResponseCodes.CLIENT_ERROR, e)
            }
        }
    }
}
