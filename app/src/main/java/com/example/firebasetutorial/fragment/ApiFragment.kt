package com.example.firebasetutorial.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.firebasetutorial.adapter.RecyclerViewAdapter
import com.example.firebasetutorial.databinding.FragmentApiBinding
import com.example.firebasetutorial.model.Photos
import com.example.firebasetutorial.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiFragment : Fragment() {

    private lateinit var binding: FragmentApiBinding
    private var list: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApiBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        binding.recView.layoutManager = GridLayoutManager(requireContext(), 2)
                        val adapter = RecyclerViewAdapter(requireContext(), list)
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