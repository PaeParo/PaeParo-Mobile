package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.manager.FirebaseManager

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // FirebaseManager 생성
        firebaseManager = FirebaseManager()

        // Firebase Auth 로그인 테스트 코드
        // Google 로그인 셋팅
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Google 로그인 창 표시
        // startForResult.launch(GoogleSignIn.getClient(this, gso).signInIntent)
    }

    // Google 로그인 결과 처리
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }

    // Google 로그인에서 얻은 ID 토큰으로 Firebase 인증 처리
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseManager.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this, "Firebase Authentication Success.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // 로그인 실패
                    Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}