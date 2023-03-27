package com.paeparo.paeparo_mobile.manager

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

@SuppressLint("StaticFieldLeak")
object FirebaseManager {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val realtimeDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
    val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
}