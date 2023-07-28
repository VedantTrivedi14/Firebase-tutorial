package com.example.firebasetutorial.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            // on below line we are
            // creating a new intent
//            binding.imgSplash.visibility = View.GONE
            if (auth.currentUser != null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.splash_fragment, ProfileFragment()).addToBackStack(null).commit()
            } else {
               parentFragmentManager.beginTransaction()
                    .replace(R.id.splash_fragment, LoginFragment()).addToBackStack(null).commit()
            }

        }, 2000)


        Log.i("#myq", "transition success")

    }

    companion object {

    }
}