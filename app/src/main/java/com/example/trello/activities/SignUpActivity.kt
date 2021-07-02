package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.R
import com.example.trello.firebase.FirestoreClass
import com.example.trello.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateForm(name:String, password:String, email:String):Boolean{
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter password")
                false
            }
            else -> true
        }
    }

    private fun registerUser(){
        val email:String = et_email_sign_up.text.toString().trim {it <= ' '}
        val name:String = et_name_sign_up.text.toString().trim {it <= ' '}
        val password:String = et_password_sign_up.text.toString().trim {it <=' '}

        if(validateForm(name,password,email)){
            showProgressDiglog("Please waiting...")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Firebase registered user
                        val firebaseUser:FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!

                        val user = User(
                            firebaseUser.uid, name, registeredEmail
                        )
                            FirestoreClass().registerUser(this,user)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()

        finish()
    }

}