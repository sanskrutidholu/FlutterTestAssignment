package com.example.fluttertestproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fluttertestproject.rvadapter.ExploreAdapter
import com.example.fluttertestproject.R
import com.example.fluttertestproject.needs.SpacingItemDecorator
import com.example.fluttertestproject.databinding.FragmentExploreBinding
import com.example.fluttertestproject.model.VideoModel
import com.google.firebase.database.*


class ExploreFragment : Fragment(R.layout.fragment_explore) {

    lateinit var binding: FragmentExploreBinding
    lateinit var videoList: ArrayList<VideoModel>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater,container,false)
        val rootView = binding.root

        videoList = ArrayList()
        binding.exploreRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            val itemDecorator = SpacingItemDecorator(15)
            addItemDecoration(itemDecorator)

        }

        loadVideo()

        return rootView
    }

    private fun loadVideo() {
        videoList.clear()
        val videoRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("AllVideos")

        videoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Error Displaying", snapshot.toString())
                videoList.clear()
                for (ds in snapshot.children){
                    Log.d("data", ds.toString())
                    val videoModel = ds.getValue(VideoModel::class.java)
                    videoList.add(videoModel!!)
                    val adapter = ExploreAdapter(context!!,videoList)
                    binding.exploreRv.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Unable To Load...", Toast.LENGTH_SHORT).show()
            }

        })
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_explore, container, false)
//    }

}