package com.example.fluttertestproject.rvadapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fluttertestproject.R
import com.example.fluttertestproject.firebaseClasses.FirebaseOperations
import com.example.fluttertestproject.model.VideoModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.snackbar.Snackbar

class LibraryAdapter(val context: Context,var videoList: ArrayList<VideoModel>): RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var playerView: PlayerView = itemView.findViewById(R.id.playerView_library)
        var delete: ImageView = itemView.findViewById(R.id.video_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.library_row_layout,parent,false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val currentData = videoList[position]
        setVideoUrl(currentData,holder)

        holder.delete.setOnClickListener {
            Snackbar.make(it, "Are you sure to delete", Snackbar.LENGTH_SHORT)
                .setAction("Delete") {

                    FirebaseOperations().deleteSinglePost(currentData.videoId, currentData.userId)
                    notifyItemRemoved(position)

                }.show()
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    private fun setVideoUrl(currentData: VideoModel, holder: LibraryViewHolder){
        val simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
        holder.playerView.player = simpleExoPlayer
        val mediaItem = MediaItem.fromUri(currentData.videoURL)
        simpleExoPlayer.addMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }
}