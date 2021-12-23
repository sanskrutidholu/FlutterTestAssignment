package com.example.fluttertestproject.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.fluttertestproject.R
import com.example.fluttertestproject.firebaseClasses.FirebaseOperations
import com.example.fluttertestproject.model.UserModel
import com.example.fluttertestproject.model.VideoModel
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class AddVideoFragment : Fragment(R.layout.fragment_add_video) {

    lateinit var title: TextInputEditText
    lateinit var desc: TextInputEditText
    lateinit var videoView: VideoView
    lateinit var camera_btn: Button
    lateinit var record_btn: Button
    lateinit var send_btn: FloatingActionButton

    lateinit var video_uri: Uri
    lateinit var mediaController: MediaController

    var PICK_VIDEO = 1
    val VIDEO_RECORD_CODE = 101

    lateinit var auth: FirebaseAuth
    lateinit var userModel: UserModel

    lateinit var uploadTask: UploadTask

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_video, container, false)

        userModel = UserModel()
        auth = FirebaseAuth.getInstance()
//        // Gets the data from the passed bundle
//        val bundle = arguments
//        userName = bundle!!.getString("UserName").toString()

        title = view.findViewById(R.id.video_title)
        desc = view.findViewById(R.id.video_desc)
        videoView = view.findViewById(R.id.video_space)
        camera_btn = view.findViewById(R.id.btn_camera)
        record_btn = view.findViewById(R.id.btn_record)

        send_btn = view.findViewById(R.id.send_btn)


        mediaController = MediaController(context)
        videoView.setMediaController(mediaController)
        videoView.start()

        camera_btn.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,PICK_VIDEO)
        }

        record_btn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent,VIDEO_RECORD_CODE)

        }

        send_btn.setOnClickListener {
            uploadVideoToStorage()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if(requestCode == VIDEO_RECORD_CODE) {
//            if(resultCode == RESULT_OK) {
//
//            }
//        }

        if (data != null) {
            video_uri = data.data!!
            videoView.setVideoURI(video_uri)
        }
    }

    private fun getfiletype(videoUri: Uri): String? {
        val contentResolver = requireActivity().contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri))
    }

    private fun uploadVideoToStorage() {

        if (video_uri != null || !TextUtils.isEmpty(title.text) || !TextUtils.isEmpty(desc.text)) {

            val reference: StorageReference = FirebaseStorage.getInstance().getReference("Videos/"+ System.currentTimeMillis() + "." + getfiletype(video_uri))
            reference.child(System.currentTimeMillis().toString() + "." + getfiletype(video_uri))
            uploadTask = reference.putFile(video_uri)

            var urlTask: Task<Uri> = uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    throw it.exception!!
                }
                return@continueWithTask reference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentTime = System.currentTimeMillis()
                    val downloadUri = it.result
                    val videoId = FirebaseDatabase.getInstance().reference.push().key.toString()
                    Toast.makeText(activity,"Video posted..",Toast.LENGTH_SHORT).show()

                    val data = VideoModel(
                        auth.currentUser!!.uid,
                        videoId,
                        title.text.toString(),
                        desc.text.toString(),
                        downloadUri.toString(),
                        currentTime,
                        title.text.toString().lowercase(),
                    )
                    FirebaseOperations().uploadVideoToDatabase(videoId,data)
                    videoView.visibility = View.INVISIBLE
                    title.text!!.isEmpty()
                    desc.text!!.isEmpty()

                } else {
                    Toast.makeText(activity,"Failed to post data...",Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            Toast.makeText(activity,"All Fields Required...",Toast.LENGTH_SHORT).show()
        }

    }

}