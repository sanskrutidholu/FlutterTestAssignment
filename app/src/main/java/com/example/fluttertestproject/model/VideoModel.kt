package com.example.fluttertestproject.model

class VideoModel (
    var userId: String,
    var videoId: String,
    var videoTitle: String,
    var videoDesc: String,
    var videoURL: String,
    var videoDate: Long,
    var search: String,

    ) {
    constructor(): this("","","","","",0,"")

}