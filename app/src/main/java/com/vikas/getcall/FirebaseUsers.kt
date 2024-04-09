package com.vikas.getcall

data class FirebaseUsers(
    var fcmID: String,
    val uid: String,
) {
    constructor() : this("", "")
}
