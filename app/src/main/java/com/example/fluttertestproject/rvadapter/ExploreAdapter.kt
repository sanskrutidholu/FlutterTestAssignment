package com.example.fluttertestproject.rvadapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fluttertestproject.R
import com.example.fluttertestproject.model.UserModel
import com.example.fluttertestproject.model.VideoModel
import com.example.fluttertestproject.needs.TimeConversion
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ExploreAdapter(val context: Context,var videoList: ArrayList<VideoModel>): RecyclerView.Adapter<ExploreAdapter.ImageViewHolder>() {

    var auth = FirebaseAuth.getInstance()
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.video_desc)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val location: TextView = itemView.findViewById(R.id.location)
        val time: TextView = itemView.findViewById(R.id.time)
        val playerView: PlayerView = itemView.findViewById(R.id.video_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.explore_item_layout,parent,false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentData = videoList[position]
        holder.title.text = currentData.videoTitle
        holder.desc.text = currentData.videoDesc
        holder.time.text = TimeConversion.getDate(currentData.videoDate)
        setVideoUrl(currentData,holder)
        setUserNameAndLocation(currentData.userId,holder)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    private fun setVideoUrl(currentData: VideoModel, holder: ImageViewHolder) {

        val simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
        holder.playerView.player = simpleExoPlayer
        val mediaItem = MediaItem.fromUri(currentData.videoURL)
        simpleExoPlayer.addMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    private fun setUserNameAndLocation(userId:String,holder: ImageViewHolder) {
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userId)

        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if(user != null) {
                        holder.userName.text = user.userName
                        holder.location.text = user.country
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//        userRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (ds in snapshot.children){
//                    val userModel = ds.getValue(UserModel::class.java)
//                    holder.userName.text = userModel!!.userName
//                    holder.location.text = userModel.country
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })

    }
}