package com.dicoding.githubuser.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fav(
    var name: String = "",
    var username: String = "",
    var location: String ="",
    var repository: String = "",
    var company: String = "",
    var followers: String = "",
    var following: String = "",
    var avatar: String = "",
    var favorite: String = ""
) : Parcelable