package com.example.firebasetutorial.fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.FragmentPhoneBinding
import com.example.firebasetutorial.view.ProfileActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class PhoneFragment : Fragment() {

    private lateinit var binding: FragmentPhoneBinding
    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth
    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var uid: String
    private val db = FirebaseFirestore.getInstance()
    private val profileRef = db.collection("profile")
    val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        addTextChangeListener()
        resendOTPTvVisibility()


        binding.countryCode.registerCarrierNumberEditText(binding.edtNumber)

        binding.btnGetOtp.setOnClickListener {
            Log.d("number", binding.edtNumber.text.toString())
            Log.d("number", binding.edtNumber.length().toString())
            if (isValidate()) {
                phoneNumber = binding.countryCode.fullNumberWithPlus.replace(" ", "")
                Log.d("number", phoneNumber)
                getOtp()
            }
        }

        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
        binding.txtResend.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
    }

    private fun getOtp() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun isValidate(): Boolean {
        var isValid = true
        if (binding.edtNumber.length() != 10) {

            Toast.makeText(requireContext(), "Enter correct number", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request

                Log.d("Tag", "onVerified failed:$e")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("Tag", "onVerified failed:$e")
            }
//

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            otp = verificationId
            resendToken = token
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("number", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later

//            storedVerificationId = verificationId
//            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
                                //this
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uid = task.result.user!!.uid
                    collection()
                    Toast.makeText(requireContext(), " authenticate successfully", Toast.LENGTH_SHORT).show()
                    val bundle  = Bundle()
                    bundle.putString("uid",uid)
                    profileFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.phone_fragment, profileFragment).commit()
//                    val intent = Intent(this, ProfileActivity::class.java)
//
//                    intent.putExtra("uid",uid)
//                    startActivity(intent)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    // Sign in failed, display a message and update the UI
                    Log.w(
                        ContentValues.TAG,
                        "signInWithCredential:failure ${task.exception.toString()}",
                        task.exception
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.edtDigitOne -> if (text.length == 1) binding.edtDigitTwo.requestFocus()
                R.id.edtDigitTwo -> if (text.length == 1) binding.edtDigitThree.requestFocus() else if (text.isEmpty()) binding.edtDigitOne.requestFocus()
                R.id.edtDigitThree -> if (text.length == 1) binding.edtDigitFour.requestFocus() else if (text.isEmpty()) binding.edtDigitTwo.requestFocus()
                R.id.edtDigitFour -> if (text.length == 1) binding.edtDigitFive.requestFocus() else if (text.isEmpty()) binding.edtDigitThree.requestFocus()
                R.id.edtDigitFive -> if (text.length == 1) binding.edtDigitSix.requestFocus() else if (text.isEmpty()) binding.edtDigitFour.requestFocus()
                R.id.edtDigitSix -> if (text.isEmpty()) binding.edtDigitFive.requestFocus()

            }
        }
    }

    private fun addTextChangeListener() {
        binding.edtDigitOne.addTextChangedListener(EditTextWatcher(binding.edtDigitOne))
        binding.edtDigitTwo.addTextChangedListener(EditTextWatcher(binding.edtDigitTwo))
        binding.edtDigitThree.addTextChangedListener(EditTextWatcher(binding.edtDigitThree))
        binding.edtDigitFour.addTextChangedListener(EditTextWatcher(binding.edtDigitFour))
        binding.edtDigitFive.addTextChangedListener(EditTextWatcher(binding.edtDigitFive))
        binding.edtDigitSix.addTextChangedListener(EditTextWatcher(binding.edtDigitSix))
    }

    private fun verifyOtp() {
        val typeOTP = (binding.edtDigitOne.text.toString() + binding.edtDigitTwo.text.toString() +
                binding.edtDigitThree.text.toString() + binding.edtDigitFour.text.toString() +
                binding.edtDigitFive.text.toString() + binding.edtDigitSix.text.toString())

        if (typeOTP.length == 6) {
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typeOTP)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(requireContext(), "Please Enter OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOTPTvVisibility() {
        binding.edtDigitOne.setText("")
        binding.edtDigitTwo.setText("")
        binding.edtDigitThree.setText("")
        binding.edtDigitFive.setText("")
        binding.edtDigitSix.setText("")
        binding.edtDigitOne.setText("")
        binding.txtResend.visibility = View.INVISIBLE
        binding.txtResend.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            binding.txtResend.visibility = View.VISIBLE
            binding.txtResend.isEnabled = true
        }, 60000)
    }

    private fun collection() {
        val profileData = hashMapOf(
            "userId" to uid
        )

        profileRef.add(profileData).addOnSuccessListener {
            Toast.makeText(requireContext(), "data submit successfully done", Toast.LENGTH_SHORT)
                .show()
        }
            .addOnFailureListener {
                Log.d("#tag", "add document with id $it")

            }
    }





    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhoneFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}