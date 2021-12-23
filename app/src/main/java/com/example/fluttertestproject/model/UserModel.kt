package com.example.fluttertestproject.model

class UserModel (
    var userId: String,
    var userName: String,
    var userPhone: String,
    var country: String,
) {
    constructor() : this("","","","")
}