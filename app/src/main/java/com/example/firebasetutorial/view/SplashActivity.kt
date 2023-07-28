package com.example.firebasetutorial.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.ActivitySplashBinding
import com.example.firebasetutorial.fragment.ProfileFragment


class SplashActivity : AppCompatActivity() {

    //    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler().postDelayed({
            // on below line we are
            // creating a new intent


        supportFragmentManager.beginTransaction().add(R.id.splashScreen, ProfileFragment()).commit()
        binding.imgSplash.visibility = View.GONE
        Log.i("#myq", "transition success")


//            if (auth.currentUser != null) {
//
////                val i = Intent(this, ProfileActivity::class.java)
////                startActivity(i)
//
//            } else {
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.splashScreen, loginFragment)
//                transaction.commit()
////                val i = Intent(this, LoginActivity::class.java)
////                startActivity(i)
//            }
            finish()
        }, 2000)
    }
}