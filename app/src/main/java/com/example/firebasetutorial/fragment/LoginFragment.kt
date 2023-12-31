package com.example.firebasetutorial.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var clint: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.txtLoginNow.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.mainActivity, RegisterFragment()).addToBackStack(null).commit()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        clint = GoogleSignIn.getClient(requireContext(), options)

        binding.btnGoogleAuth.setOnClickListener {
            val intent = clint.signInIntent
            startActivityForResult(intent, 10001)
        }
//
        binding.btnPhoneAuth.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainActivity, PhoneFragment()).addToBackStack(null).commit()

        }
    }

    private fun performLogin() {
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString().trim())
                .matches() || binding.edtPwd.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(
            binding.edtEmail.text.toString(),
            binding.edtPwd.text.toString()
        )
            //this
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success,  inflate to main activity
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainActivity, ProfileFragment()).addToBackStack(null).commit()
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    startActivity(intent)

                    Toast.makeText(
                        requireContext(),
                        "Success",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
}