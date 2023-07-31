package com.example.firebasetutorial.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.FragmentProfileBinding
import com.example.firebasetutorial.view.ApiActivity
import com.example.firebasetutorial.view.LoginActivity
import com.example.firebasetutorial.view.MyProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment() {
    private lateinit var uri: Uri
    private val db = FirebaseFirestore.getInstance()
    private val profileRef = db.collection("profile")
    private var storageRef = Firebase.storage

    private val auth = FirebaseAuth.getInstance()
    private val myProfileFragment = MyProfileFragment()

    private lateinit var binding: FragmentProfileBinding
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                                    requireContext(),
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
                                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT)
                                    .show()
                            }.addOnFailureListener { _ ->
                                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "it is not uploaded", Toast.LENGTH_SHORT)
                            .show()
                    }


            }
        }

        binding.btnRetrieve.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.currentUser!!.uid
            profileRef.whereEqualTo("userId", auth.currentUser!!.uid).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.i("#azx", document.id)
                        val profileDocumentRef = profileRef.document(document.id)
                        val userDataRef = profileDocumentRef.collection("user data")
                        userDataRef.get().addOnSuccessListener {
                            for (doc in it.documents) {

                                val documentRef = userDataRef.document(doc.id)
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
                                            val bundle = Bundle()
                                            bundle.putString("name", name)
                                            bundle.putString("age", age)
                                            bundle.putString("url", url)
                                            myProfileFragment.arguments = bundle
                                            requireActivity().supportFragmentManager.beginTransaction()
                                                .replace(R.id.mainActivity, myProfileFragment)
                                                .addToBackStack(null)
                                                .commit()

//                                            val intent = Intent(this, MyProfileActivity::class.java)
//                                            intent.putExtra("name", name)
//                                            intent.putExtra("age", age)
//                                            intent.putExtra("url", url)
//                                            startActivity(intent)

                                        } else {
                                            // Document doesn't exist
                                            Toast.makeText(
                                                requireContext(),
                                                "No data found",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    } else {
                                        // An error occurred while fetching the document
                                        Toast.makeText(
                                            requireContext(),
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
                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isValidate(): Boolean {

        var isValid = true
        when {
            binding.edtName.text.toString().isEmpty() -> {
                Toast.makeText(requireContext(), "Enter Name", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            binding.edtName.text.toString().isEmpty() -> {
                Toast.makeText(requireContext(), "Enter Age", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            uri.toString().isEmpty() -> {
                Toast.makeText(requireContext(), "upload Image", Toast.LENGTH_SHORT).show()
                isValid = false
            }
        }
        return isValid
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.logout_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainActivity, LoginFragment()).addToBackStack(null).commit()
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
            }

            R.id.menu_api -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainActivity, ApiFragment()).addToBackStack(null).commit()
//                val intent = Intent(this, ApiActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}