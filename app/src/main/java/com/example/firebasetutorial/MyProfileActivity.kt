package com.example.firebasetutorial

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.firebasetutorial.databinding.ActivityMyProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private var list: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("#url", intent.extras!!.getString("url").toString())
        binding.txtName.text = intent.extras!!.getString("name")
        binding.txtAge.text = intent.extras!!.getString("age")
        Glide.with(this).load(intent.extras!!.getString("url"))
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .into(binding.imgProfile)
        getData()
    }

    private fun getData() {

        RetrofitInstance.api.getData().enqueue(object : Callback<List<Photos>> {

            override fun onResponse(call: Call<List<Photos>>, response: Response<List<Photos>>) {

                if (response.isSuccessful) {
//                    Log.i("#jkl","On response :${response.body()}")
                    response.body()?.let {

                        for (data in it) {
                            Log.i("#jkl", "On response: ${data.urls.regular}")
                            list.add(data.urls.regular)

                        }
                        Log.i("#zxc", list.size.toString())
                        Log.i("#zxc", list.toString())
                        binding.recView.layoutManager = GridLayoutManager(this@MyProfileActivity, 2)
                        val adapter = RecyclerViewAdapter(this@MyProfileActivity, list)
                        binding.recView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Photos>>, t: Throwable) {
                Log.i("#jkl", "on failure : ${t.message}")
            }

        })

    }
}


