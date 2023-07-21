package com.example.firebasetutorial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetutorial.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var uri: Uri
    private val db = FirebaseFirestore.getInstance()
    private val profileRef = db.collection("profile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val galleryImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imgProfile.setImageURI(it)
                uri = it!!
            }
        binding.imgProfile.setOnClickListener { galleryImage.launch("image/*") }


        binding.btnSubmit.setOnClickListener {
            if (isValidate()) {
                val profileData = hashMapOf(
                    "name" to binding.edtName.text.toString(),
                    "age" to binding.edtAge.text.toString(),
                    "url" to uri
                )

                profileRef.add(profileData).addOnSuccessListener {
                    Toast.makeText(this, "data submit successfully done", Toast.LENGTH_SHORT)
                        .show()
                }
                    .addOnFailureListener {
                        Log.d("tag", "add document with id $it")
                    }
            }
        }

        binding.btnRetrieve.setOnClickListener {
            profileRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val documentId = document.id

                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValid = true
        when {
            binding.edtName.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            binding.edtName.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter Age", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            uri.toString().isEmpty() -> {
                Toast.makeText(this, "upload Image", Toast.LENGTH_SHORT).show()
                isValid = false
            }
        }
        return isValid
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}