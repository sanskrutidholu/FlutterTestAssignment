package com.example.fluttertestproject.firebaseClasses

import com.example.fluttertestproject.model.UserModel
import com.example.fluttertestproject.model.VideoModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseOperations {

    private val database = FirebaseDatabase.getInstance()

    fun registerUser (userId:String,userName:String, userPhone:String, country:String) {
        val userDetails = UserModel(userId, userName, userPhone, country)
        val userRef : DatabaseReference = database.getReference("Users")
        userRef.child(userId).setValue(userDetails)
    }

    fun uploadVideoToDatabase(videoId: String,videoModel: VideoModel) {
        val videoDetails = VideoModel(
            videoModel.userId,
            videoId,
            videoModel.videoTitle,
            videoModel.videoDesc,
            videoModel.videoURL,
            videoModel.videoDate,
            videoModel.search
        )


        val allVideosRef : DatabaseReference = database.getReference("AllVideos")
        allVideosRef.child(videoId).setValue(videoDetails)

        val videoRef: DatabaseReference = database.getReference("UserVideos").child(videoModel.userId)
        videoRef.child(videoId).setValue(videoDetails)

    }

    fun deleteSinglePost(postId: String,userId: String) {
        database.getReference("UserVideos").child(userId).child(postId).removeValue()
        database.getReference("AllVideos").child(postId).removeValue()
    }
}