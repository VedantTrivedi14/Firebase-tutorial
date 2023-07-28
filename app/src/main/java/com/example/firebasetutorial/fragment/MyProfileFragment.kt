package com.example.firebasetutorial.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.firebasetutorial.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private lateinit var binding : FragmentMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        assert(arguments != null)
        val name  = requireArguments().getString("name")
        binding.txtName.text = requireArguments().getString("name")
        binding.txtAge.text =  requireArguments().getString("age")


            Log.i("#url", requireArguments().getString("url").toString())
//        binding.txtName.text = intent.extras!!.getString("name")
//        binding.txtAge.text = intent.extras!!.getString("age")

        Glide.with(requireContext())
            .load(requireArguments().getString("url").toString())
            .into(binding.imgProfile)

        Glide.with(this).load(requireArguments().getString("url").toString())
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .into(binding.imgProfile)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}