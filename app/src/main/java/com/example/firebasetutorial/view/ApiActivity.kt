package com.example.firebasetutorial.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.firebasetutorial.model.Photos
import com.example.firebasetutorial.adapter.RecyclerViewAdapter
import com.example.firebasetutorial.retrofit.RetrofitInstance
import com.example.firebasetutorial.databinding.ActivityApiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApiBinding
    private var list: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
    }

    private fun getData() {

        RetrofitInstance.api.getData().enqueue(object : Callback<List<Photos>> {

            override fun onResponse(call: Call<List<Photos>>, response: Response<List<Photos>>) {

                if (response.isSuccessful) {
//                    Log.i("#jkl", "On response :${response.body()}")
                    response.body()?.let {

                        for (data in it) {
                            Log.i("#jkl", "On response: ${data.urls.regular}")
                            list.add(data.urls.regular)

                        }
                        Log.i("#zxc", list.size.toString())
                        Log.i("#zxc", list.toString())
                        binding.recView.layoutManager = GridLayoutManager(this@ApiActivity, 2)
                        val adapter = RecyclerViewAdapter(this@ApiActivity, list)
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