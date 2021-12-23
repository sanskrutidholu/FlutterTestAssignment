package com.example.fluttertestproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.fluttertestproject.R
import com.example.fluttertestproject.databinding.FragmentExploreBinding
import com.example.fluttertestproject.databinding.FragmentLibraryBinding
import com.example.fluttertestproject.model.UserModel
import com.example.fluttertestproject.model.VideoModel
import com.example.fluttertestproject.needs.SpacingItemDecorator
import com.example.fluttertestproject.rvadapter.ExploreAdapter
import com.example.fluttertestproject.rvadapter.LibraryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LibraryFragment : Fragment(R.layout.fragment_library) {

    lateinit var binding: FragmentLibraryBinding
    lateinit var auth: FirebaseAuth
    lateinit var userModel: UserModel
    lateinit var videoList: ArrayList<VideoModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater,container,false)
        val rootView = binding.root

        auth = FirebaseAuth.getInstance()

        fetchUserDetails()

        videoList = ArrayList()
        binding.rvLibrary.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
//            val itemDecorator = SpacingItemDecorator(15)
//            addItemDecoration(itemDecorator)
        }
        fetchUserPosts()

        return rootView
    }

    private fun fetchUserDetails() {
        val userRef: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(auth.currentUser!!.uid)

        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userModel = snapshot.getValue(UserModel::class.java)!!
                binding.usernameLibrary.text = userModel.userName
                binding.locationLibrary.text = userModel.country
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchUserPosts(){
        videoList.clear()
        val videoRf: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("UserVideos")
            .child(auth.currentUser!!.uid)

        videoRf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                videoList.clear()
                for (ds in snapshot.children){
                    val videoModel = ds.getValue(VideoModel::class.java)
                    videoList.add(videoModel!!)
                    val adapter = LibraryAdapter(context!!,videoList)
                    binding.rvLibrary.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Unable To Load...",Toast.LENGTH_SHORT).show()
            }

        })

    }

}