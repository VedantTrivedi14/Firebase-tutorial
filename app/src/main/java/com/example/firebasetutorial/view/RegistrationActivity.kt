package com.example.firebasetutorial.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.ActivityRegistrationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var clint: GoogleSignInClient
    lateinit var uid: String
    private val db = FirebaseFirestore.getInstance()
    private val profileRef = db.collection("profile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.txtRegisterNow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            performSignUp()

        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        clint = GoogleSignIn.getClient(this, options)
        binding.btnGoogleAuth.setOnClickListener {
            val intent = clint.signInIntent
            startActivityForResult(intent, 10001)

        }

        binding.btnPhoneAuth.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Log.i("abcd....", task.result.toString())
                        uid = task.result.user!!.uid
                        collection()
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("uid",uid)
                        startActivity(intent)

                        Toast.makeText(this, "successfully Register", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performSignUp() {

        if ((binding.edtEmail.text!!.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString().trim())
                .matches()) || binding.edtPwd.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(
            binding.edtEmail.text.toString(),
            binding.edtPwd.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success,inflate to Login activity
                    uid = task.result.user!!.uid
                    collection()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("uid",uid)
                    startActivity(intent)
                    Toast.makeText(
                        baseContext,
                        "Success",
                        Toast.LENGTH_SHORT,
                    ).show()

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun collection() {
        val profileData = hashMapOf(
            "userId" to uid
        )

        profileRef.add(profileData).addOnSuccessListener {
            Toast.makeText(this, "data submit successfully done", Toast.LENGTH_SHORT)
                .show()
        }
            .addOnFailureListener {
                Log.d("#tag", "add document with id $it")

            }


    }


}