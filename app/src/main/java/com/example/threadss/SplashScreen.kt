package com.example.threadss

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.threadss.UserSetup.ProfileSetUpActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        auth = FirebaseAuth.getInstance()


        Handler(Looper.myLooper()!!).postDelayed({
            if (auth.currentUser != null) {
                startActivity(Intent(this , MainActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this , ProfileSetUpActivity::class.java))
                finish()
            }
        },3000)


    }
}