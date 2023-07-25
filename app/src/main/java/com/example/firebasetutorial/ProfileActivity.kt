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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var uri: Uri
    private val db = FirebaseFirestore.getInstance()
    private val profileRef = db.collection("profile")
    private var storageRef = Firebase.storage

    //    private lateinit var uid: String
    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        uid = intent.extras!!.getString("uid").toString()
        val galleryImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imgProfile.setImageURI(it)
                uri = it!!
            }
        binding.imgProfile.setOnClickListener { galleryImage.launch("image/*") }


        binding.btnSubmit.setOnClickListener {
            if (isValidate()) {
                profileRef.get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        if (document.get("userId") == auth.currentUser!!.uid) {
                            val documentId = document.id
                            Log.i("#xyz", documentId)
                            val profileDocumentRef = profileRef.document(documentId)
                            val userDataRef = profileDocumentRef.collection("user data")
                            val userData = hashMapOf(
                                "name" to binding.edtName.text.toString(),
                                "age" to binding.edtAge.text.toString(),
                                "url" to uri
                            )
                            userDataRef.add(userData).addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "sub collection data submit successfully done",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                                .addOnFailureListener {
                                    Log.d("tag", "add document with id $it")
                                }
                        }

                    }
                }

                storageRef.getReference("profileImage").child(System.currentTimeMillis().toString())
                    .putFile(uri).addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            val uerId = FirebaseAuth.getInstance().currentUser!!.uid
                            val myUri = it
                            val mapImage = mapOf(
                                "url" to it.toString()
                            )

                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("ProfileImages")
                            databaseReference.child(uerId).setValue(mapImage).addOnSuccessListener {
                                Log.i("image", myUri.toString())
                                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener { _ ->
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this, "it is not uploaded", Toast.LENGTH_SHORT).show()
                    }


            }
        }

        binding.btnRetrieve.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.currentUser!!.uid
            profileRef.whereEqualTo("userId", auth.currentUser!!.uid).get()
                .addOnSuccessListener { it ->
                    for (document in it) {
                        Log.i("#azx", document.id)
                        val profileDocumentRef = profileRef.document(document.id)
                        val userDataRef = profileDocumentRef.collection("user data")
                        userDataRef.get().addOnSuccessListener { it ->
                            for (document in it.documents) {

                                val documentRef = userDataRef.document(document.id)
                                documentRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val documentSnapshot = task.result
                                        if (documentSnapshot.exists()) {
                                            // The document exists, and you can access its fields here
                                            val name: String =
                                                documentSnapshot.getString("name").toString()
                                            val age = documentSnapshot.getString("age").toString()
                                            val url = documentSnapshot.getString("url")
                                            Log.i("#asd", name)
                                            Log.i("#asd", age)
                                            Log.i("#asd", url.toString())
                                            val intent = Intent(this, MyProfileActivity::class.java)
                                            intent.putExtra("name", name)
                                            intent.putExtra("age", age)
                                            intent.putExtra("url", url)
                                            startActivity(intent)

                                        } else {
                                            // Document doesn't exist
                                            Toast.makeText(
                                                this,
                                                "No data found",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    } else {
                                        // An error occurred while fetching the document
                                        Toast.makeText(
                                            this,
                                            "error while fetching data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                            .addOnFailureListener { }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
        }


//        binding.btnRetrieve.setOnClickListener {
//            profileRef.get().addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot.documents) {
//                    val documentId = document.id
//                    val profileDocumentRef = profileRef.document(documentId)
//                    val userDataRef = profileDocumentRef.collection("user data")
//                    //one
//                    userDataRef.get().addOnSuccessListener { querySnapshot ->
//                        for (document in querySnapshot.documents) {
//                            val documentId = document.id
//                            Log.e("#pqr", documentId)
//
//                            val documentRef = userDataRef.document(documentId)
//                            documentRef.get().addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    val documentSnapshot = task.result
//                                    if (documentSnapshot.exists()) {
//                                        // The document exists, and you can access its fields here
//                                        val name: String =
//                                            documentSnapshot.getString("name").toString()
//                                        val age = documentSnapshot.getString("age").toString()
//                                        val url = documentSnapshot.getString("url").toString()
//                                        Log.i("#asd", name)
//                                        Log.i("#asd", age)
//                                        Log.i("#asd", url)
//
//                                    } else {
//                                        // Document doesn't exist
//                                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT)
//                                            .show()
//                                    }
//                                } else {
//                                    // An error occurred while fetching the document
//                                    Toast.makeText(
//                                        this,
//                                        "error while fetching data",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }

//
//                        }
//
//                    }
//                    //two
////                    userDataRef.get().addOnSuccessListener { result ->
////                        if (result != null) {
//////                                    val usersList = mutableListOf<User>()
////
////                            for (document in result) {
////                                document
////
////                                val name = document.getString("name")
////                                val age = document.getString("age")
////                                val url = document.getString("url")
////                                if (name != null && age != null && url != null) {
////                                    Log.i("#asd", name)
////                                    Log.i("#asd", age)
////                                    Log.i("#asd", url)
//                                    val intent = Intent(this, MyProfileActivity::class.java)
//                                    intent.putExtra("name", name)
//                                    intent.putExtra("age", age)
//                                    intent.putExtra("url", url)
//                                    startActivity(intent)
////
//////                                            val user = User(name)
//////                                            usersList.add(user)
////                                }
////                            }
////
////                            // Now you have the list of users with names
////                            // Do something with the list here
////                        } else {
////                            // No data found
////                            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
////                        }
////                    }
////                        .addOnFailureListener {
////                            Toast.makeText(
////                                this,
////                                "user data sub-collection not found",
////                                Toast.LENGTH_SHORT
////                            ).show()
////
////                        }
//
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "profile collection not found", Toast.LENGTH_SHORT).show()
//            }
//        }
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