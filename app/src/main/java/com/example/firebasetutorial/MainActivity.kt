package com.example.firebasetutorial

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.firebasetutorial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val userRef = db.collection("user")
    private var storageRef = Firebase.storage
    private lateinit var uri: Uri
    private lateinit var uid: String
    private lateinit var documentReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance()

        binding.btnGetData.setOnClickListener {
            userRef.get().addOnCompleteListener {

                val result = StringBuffer()
                if (it.isSuccessful) {
                    for (document in it.result) {
                        Log.i("id", document.id)
                        result.append(document.data.getValue("firstName")).append(" ")
                            .append(document.data.getValue("lastName")).append("\n")
                    }
                }
                binding.txtGetData.text = result
            }
        }

        binding.btnSave.setOnClickListener {
            val user: MutableMap<String, Any> = HashMap()
            user["firstName"] = binding.edtFirstName.text.toString()
            user["lastName"] = binding.edtLastName.text.toString()
            userRef.add(user).addOnSuccessListener {
                Log.d("tag", "add document with id ${it.id}")
                Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show()
                uid = it.id

            }.addOnFailureListener {
                Log.d("tag", "add document with id $it")
            }
            binding.edtFirstName.text?.clear()
            binding.edtLastName.text?.clear()
        }

        val galleryImage =
            registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
                binding.imgProfile.setImageURI(it)
                uri = it!!
            })
        binding.btnBrowseImage.setOnClickListener {
            galleryImage.launch("image/*")
        }

        binding.btnUploadImage.setOnClickListener {
            storageRef.getReference("images").child(System.currentTimeMillis().toString())
                .putFile(uri).addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        val uerId = FirebaseAuth.getInstance().currentUser!!.uid
                        val myUri = it
                        val mapImage = mapOf(
                            "url" to it.toString()
                        )

                        val databaseReference =
                            FirebaseDatabase.getInstance().getReference("userImages")
                        databaseReference.child(uerId).setValue(mapImage).addOnSuccessListener {
                            Log.i("image", myUri.toString())
                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                            createSubCollection(uid)
                        }.addOnFailureListener { _ ->
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "it is not uploaded", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnRetrieveImage.setOnClickListener {
            userRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val documentId = document.id
                    // Do something with the document ID
                    // For example, print it:
//                        println("Document ID: $documentId")

                   val documentReference = userRef.document(documentId)
                    fun hasSubCollections(
                        documentReference: DocumentReference, onComplete: (Boolean) -> Unit
                    ) {
                        documentReference.collection("profile picture url") // Replace "subCollectionName" with the name of the subCollection you expect
                            .limit(1) // Limit the query to fetch only one document
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val querySnapshot: QuerySnapshot? = task.result
                                    val hasSubCollections =
                                        querySnapshot != null && !querySnapshot.isEmpty
                                    onComplete(hasSubCollections)
                                } else {
                                    // Handle the error if the query fails
                                    onComplete(false)
                                }
                            }
                    }

                    hasSubCollections(documentReference) { hasSubCollections ->
                        if (hasSubCollections) {
                            val imagesCollectionRef =
                                documentReference.collection("profile picture url")
                            imagesCollectionRef.get().addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot.documents) {
                                    val imageUrl = document.getString("profilePictureUrl")
                                    Log.i("vedant~~~", imageUrl.toString())
                                    // Do something with the imageUrl, like loading the image using a library like Glide or Picasso
                                    if(uri.toString()==imageUrl.toString()){
                                        Glide.with(this).load(imageUrl)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                                            .into(binding.imgRetrieve)
                                    }

                                }
                            }.addOnFailureListener { exception ->
                                Log.e("error", "Error getting documents: $exception")
                            }

                        } else {
                            Log.i("message", ": $documentReference do not have sub collection")
                        }
                    }

                }


            }.addOnFailureListener { exception ->
                // Handle any errors that occurred during the data retrieval
                Log.e("error", "Error getting documents: $exception")

            }
        }


    }


    private fun createSubCollection(id: String) {
        val documentRef = userRef.document(id)
        val subCollectionRef = documentRef.collection("profile picture url")
        val subCollectionData = hashMapOf(
            "profilePictureUrl" to uri
        )

        subCollectionRef.add(subCollectionData).addOnSuccessListener {
            Toast.makeText(this, "sub-collection created successfully", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            Log.d("tag", "add document with id $it")
        }


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



