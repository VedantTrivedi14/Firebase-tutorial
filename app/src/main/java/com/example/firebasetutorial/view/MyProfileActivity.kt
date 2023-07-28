package com.example.firebasetutorial.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.firebasetutorial.databinding.ActivityMyProfileBinding


class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("#url", intent.extras!!.getString("url").toString())
        binding.txtName.text = intent.extras!!.getString("name")
        binding.txtAge.text = intent.extras!!.getString("age")

        Glide.with(this)
            .load(intent.extras!!.getString("url").toString())
            .into(binding.imgProfile)

        Glide.with(this).load(intent.extras!!.getString("url").toString())
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .into(binding.imgProfile)
    }
}


