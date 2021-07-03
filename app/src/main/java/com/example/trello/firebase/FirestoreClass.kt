package com.example.trello.firebase

import android.app.Activity
import android.util.Log
import com.example.trello.activities.MainActivity
import com.example.trello.activities.SignInActivity
import com.example.trello.activities.SignUpActivity
import com.example.trello.model.User
import com.example.trello.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity, userInfo : User){
        mFireStore.collection(Constants.USER)
            .document(getCurrentId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                    e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e)
            }
    }

    fun signInUser(activity: Activity){
        mFireStore.collection(Constants.USER)
            .document(getCurrentId())
            .get()
            .addOnSuccessListener { document->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }

            }
            .addOnFailureListener { e ->
                when(activity){
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun getCurrentId(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

}