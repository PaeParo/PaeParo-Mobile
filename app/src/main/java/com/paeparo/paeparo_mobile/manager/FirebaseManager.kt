package com.paeparo.paeparo_mobile.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
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

    private val firestoreUsersRef = firestore.collection("users")
    private val firestoreTripsRef = firestore.collection("trips")
    private val firestoreEventsRef = firestore.collection("events")
    private val firestorePostsRef = firestore.collection("posts")

    /**
     * FirebaseManager 사용 전 객체 내 요소들을 초기화하는 함수
     *
     * @param context Application Context
     */
    fun initializeManager(context: Context) {
        applicationContext = context.applicationContext
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
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
        context: Context,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
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
     * Google 계정을 이용하여 로그인하는 함수
     *
     * @param context 해당 로그인 함수를 실행할 Activity의 Context
     * @param googleSignInLauncher 이전에 생성한 ActivityResultLauncher<Intent> (FirebaseManager.createGoogleLoginLauncher()을 이용하여 생성한 객체)
     */
    fun loginWithGoogle(
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
     * 사용자 등록 여부를 확인하여 Firestore에 사용자를 추가하는 함수
     *
     * @return 등록 및 세부정보 입력 상태에 대한 확인 결과값
     */
    suspend fun checkUserRegistered(context: Context): Result<FirebaseConstants.RegistrationStatus> {
        try {
            val documentSnapshot =
                firestoreUsersRef.document(context.getPaeParo().userId).get().await()

            if (!documentSnapshot.exists()) { // 사용자가 등록 되어 있지 않을 경우, 사용자 등록 및 NICKNAME_NOT_REGISTERED 반환
                val newUser = PaeParoUser()
                firestoreUsersRef.document(context.getPaeParo().userId)
                    .set(newUser.toMap())
                    .await()
                return Result.success(FirebaseConstants.RegistrationStatus.NICKNAME_NOT_REGISTERED)
            } else { // 사용자가 등록되어 있을 경우
                val user = documentSnapshot.toObject(PaeParoUser::class.java)

                return if (user!!.nickname == "") { // 사용자 닉네임이 설정 되어 있지 않을 경우
                    Result.success(FirebaseConstants.RegistrationStatus.NICKNAME_NOT_REGISTERED)
                } else if (user.age == 0) { // 사용자 세부정보가 설정 되어 있지 않을 경우
                    Result.success(FirebaseConstants.RegistrationStatus.DETAIL_INFO_NOT_REGISTERED)
                } else { // 사용자 정보가 모두 설정 되어 있을 경우
                    Result.success(FirebaseConstants.RegistrationStatus.REGISTERED)
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
